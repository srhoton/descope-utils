package com.descope.utils.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.enums.NodeExpressionType;
import com.descope.enums.NodeType;
import com.descope.exception.DescopeException;
import com.descope.model.authz.Namespace;
import com.descope.model.authz.Node;
import com.descope.model.authz.NodeExpression;
import com.descope.model.authz.RelationDefinition;
import com.descope.model.authz.Schema;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.rebac.NamespaceModel;
import com.descope.utils.model.rebac.RelationDefinitionModel;
import com.descope.utils.model.rebac.SchemaModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing Descope ReBAC (Relationship-Based Access Control) authorization schemas.
 *
 * <p>Provides operations to create, load, and delete authorization schemas with idempotency
 * support.
 */
@ApplicationScoped
public class AuthzService {

  private static final Logger logger = LoggerFactory.getLogger(AuthzService.class);

  private final DescopeService descopeService;
  private final ObjectMapper objectMapper;

  /**
   * Creates a new AuthzService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public AuthzService(DescopeService descopeService) {
    this.descopeService = descopeService;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Creates or updates a ReBAC schema from a JSON file.
   *
   * <p>This method loads the schema definition from a JSON file and creates or updates it in
   * Descope. The schema is always created with upgrade=true to allow modifications to existing
   * schemas.
   *
   * @param config The Descope configuration
   * @param schemaFile The path to the JSON file containing the schema definition
   * @param upgrade Whether to upgrade an existing schema (default: true)
   * @return OperationResult containing the schema operation status
   */
  public OperationResult<SchemaModel> createSchema(
      DescopeConfig config, String schemaFile, boolean upgrade) {
    logger.info("Creating/updating ReBAC schema from file: {}", schemaFile);

    try {
      // Read schema from JSON file
      SchemaModel schemaModel = readSchemaFromFile(schemaFile);

      // Convert model to SDK Schema
      Schema schema = convertToSdkSchema(schemaModel);

      // Get AuthzService from SDK
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      // Check if schema already exists (for idempotency)
      try {
        Schema existingSchema = sdkAuthzService.loadSchema();
        if (existingSchema != null && existingSchema.getNamespaces() != null) {
          logger.info("Schema already exists, updating with upgrade={}", upgrade);
        }
      } catch (DescopeException e) {
        // Schema doesn't exist, which is fine
        logger.debug("No existing schema found, will create new schema");
      }

      // Save the schema (create or update)
      sdkAuthzService.saveSchema(schema, upgrade);

      logger.info(
          "Successfully created/updated ReBAC schema with {} namespaces",
          schemaModel.getNamespaces().size());
      return OperationResult.created(
          schemaModel, "Schema created/updated successfully with upgrade=" + upgrade);

    } catch (IOException e) {
      String message = "Failed to read schema file: " + e.getMessage();
      logger.error(message, e);
      throw new RuntimeException(message, e);
    } catch (DescopeException e) {
      throw descopeService.wrapException("create/update ReBAC schema", e);
    }
  }

  /**
   * Loads the current ReBAC schema from Descope.
   *
   * @param config The Descope configuration
   * @return OperationResult containing the current schema, or an error if no schema exists
   */
  public OperationResult<SchemaModel> loadSchema(DescopeConfig config) {
    logger.info("Loading current ReBAC schema");

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      Schema schema = sdkAuthzService.loadSchema();

      if (schema == null) {
        logger.info("No schema found");
        return OperationResult.failure("No schema exists");
      }

      // Convert SDK schema to model
      SchemaModel schemaModel = convertFromSdkSchema(schema);

      logger.info(
          "Successfully loaded schema with {} namespaces", schemaModel.getNamespaces().size());
      return OperationResult.success(schemaModel, "Schema loaded successfully");

    } catch (DescopeException e) {
      if (e.getMessage() != null && e.getMessage().contains("not found")) {
        logger.info("No schema found");
        return OperationResult.failure("No schema exists");
      }
      throw descopeService.wrapException("load ReBAC schema", e);
    }
  }

  /**
   * Deletes the current ReBAC schema from Descope.
   *
   * @param config The Descope configuration
   * @return OperationResult indicating success or failure
   */
  public OperationResult<String> deleteSchema(DescopeConfig config) {
    logger.info("Deleting ReBAC schema");

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      // Check if schema exists
      try {
        Schema existing = sdkAuthzService.loadSchema();
        if (existing == null
            || existing.getNamespaces() == null
            || existing.getNamespaces().isEmpty()) {
          logger.info("No schema found to delete");
          return OperationResult.failure("No schema exists to delete");
        }
      } catch (DescopeException e) {
        logger.info("No schema found to delete");
        return OperationResult.failure("No schema exists to delete");
      }

      // Delete the schema
      sdkAuthzService.deleteSchema();

      logger.info("Successfully deleted ReBAC schema");
      return OperationResult.created("", "Schema deleted successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("delete ReBAC schema", e);
    }
  }

  /**
   * Reads a schema from a JSON file.
   *
   * @param filePath The path to the JSON file
   * @return The parsed SchemaModel
   * @throws IOException If file reading fails
   */
  private SchemaModel readSchemaFromFile(String filePath) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) {
      throw new IOException("Schema file not found: " + filePath);
    }
    return objectMapper.readValue(file, SchemaModel.class);
  }

  /**
   * Converts our domain model Schema to SDK Schema.
   *
   * <p>This creates a simple Node-based expression for each relation where targetNamespaces are
   * converted to a UNION of SELF expressions (each target namespace can access the relation).
   *
   * @param schemaModel The domain model schema
   * @return The SDK Schema
   */
  private Schema convertToSdkSchema(SchemaModel schemaModel) {
    List<Namespace> namespaces = new ArrayList<>();

    for (NamespaceModel nsModel : schemaModel.getNamespaces()) {
      List<RelationDefinition> relationDefs = new ArrayList<>();

      for (RelationDefinitionModel relModel : nsModel.getRelationDefinitions()) {
        // Create a Node for each target namespace
        // For simplicity, if there's one target namespace, create a SELF expression
        // If there are multiple, create a UNION node
        Node complexDefinition;

        if (relModel.getTargetNamespaces().isEmpty()) {
          // No targets specified - create an empty node
          complexDefinition = null;
        } else if (relModel.getTargetNamespaces().size() == 1) {
          // Single target - create a SELF expression node
          String targetNs = relModel.getTargetNamespaces().get(0);
          NodeExpression expr =
              new NodeExpression(NodeExpressionType.SELF, null, null, relModel.getName(), targetNs);
          complexDefinition = new Node(NodeType.CHILD, null, expr);
        } else {
          // Multiple targets - create a UNION node with child SELF expressions
          List<Node> children = new ArrayList<>();
          for (String targetNs : relModel.getTargetNamespaces()) {
            NodeExpression expr =
                new NodeExpression(
                    NodeExpressionType.SELF, null, null, relModel.getName(), targetNs);
            children.add(new Node(NodeType.CHILD, null, expr));
          }
          complexDefinition = new Node(NodeType.UNION, children, null);
        }

        relationDefs.add(new RelationDefinition(relModel.getName(), complexDefinition));
      }

      namespaces.add(new Namespace(nsModel.getName(), relationDefs));
    }

    return new Schema(schemaModel.getName(), namespaces);
  }

  /**
   * Converts SDK Schema to our domain model Schema.
   *
   * <p>This extracts target namespaces from the Node-based expressions in a simplified way. For
   * complex expressions, only SELF-type expressions are extracted.
   *
   * @param sdkSchema The SDK Schema
   * @return The domain model schema
   */
  private SchemaModel convertFromSdkSchema(Schema sdkSchema) {
    List<NamespaceModel> namespaces = new ArrayList<>();

    for (Namespace ns : sdkSchema.getNamespaces()) {
      List<RelationDefinitionModel> relationDefs = new ArrayList<>();

      for (RelationDefinition relDef : ns.getRelationDefinitions()) {
        // Extract target namespaces from the complex definition
        List<String> targetNamespaces = extractTargetNamespaces(relDef.getComplexDefinition());
        relationDefs.add(new RelationDefinitionModel(relDef.getName(), targetNamespaces));
      }

      namespaces.add(new NamespaceModel(ns.getName(), relationDefs));
    }

    return new SchemaModel(null, namespaces);
  }

  /**
   * Extracts target namespaces from a Node-based relation definition.
   *
   * <p>This is a simplified extraction that handles SELF expressions and UNION nodes. More complex
   * expressions are not fully represented.
   *
   * @param node The Node to extract from
   * @return List of target namespace names
   */
  private List<String> extractTargetNamespaces(Node node) {
    if (node == null) {
      return new ArrayList<>();
    }

    List<String> targets = new ArrayList<>();

    if (node.getNType() == NodeType.CHILD && node.getExpression() != null) {
      NodeExpression expr = node.getExpression();
      if (expr.getNeType() == NodeExpressionType.SELF
          && expr.getTargetRelationDefinitionNamespace() != null) {
        targets.add(expr.getTargetRelationDefinitionNamespace());
      }
    } else if (node.getNType() == NodeType.UNION && node.getChildren() != null) {
      for (Node child : node.getChildren()) {
        targets.addAll(extractTargetNamespaces(child));
      }
    }

    return targets;
  }
}
