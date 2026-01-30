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
import com.descope.utils.model.fga.RelationQueryModel;
import com.descope.utils.model.fga.RelationTupleModel;
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
   * <p>This handles both simple targetNamespaces-based relations and complex computed relations
   * defined via complexDefinition.
   *
   * @param schemaModel The domain model schema
   * @return The SDK Schema
   */
  private Schema convertToSdkSchema(SchemaModel schemaModel) {
    List<Namespace> namespaces = new ArrayList<>();

    for (NamespaceModel nsModel : schemaModel.getNamespaces()) {
      List<RelationDefinition> relationDefs = new ArrayList<>();

      for (RelationDefinitionModel relModel : nsModel.getRelationDefinitions()) {
        Node complexDefinition;

        // Check if a complex definition is provided
        if (relModel.getComplexDefinition() != null) {
          // Use the provided complex definition
          complexDefinition = convertNodeModelToSdkNode(relModel.getComplexDefinition());
        } else if (relModel.getTargetNamespaces().isEmpty()) {
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
   * Converts a NodeModel to SDK Node.
   *
   * @param nodeModel The node model to convert
   * @return The SDK Node
   */
  private Node convertNodeModelToSdkNode(com.descope.utils.model.rebac.NodeModel nodeModel) {
    if (nodeModel == null) {
      return null;
    }

    // Convert node type
    NodeType nType = null;
    if (nodeModel.getNType() != null) {
      switch (nodeModel.getNType().toLowerCase()) {
        case "child":
          nType = NodeType.CHILD;
          break;
        case "union":
          nType = NodeType.UNION;
          break;
        case "intersect":
          nType = NodeType.INTERSECT;
          break;
        case "sub":
          nType = NodeType.SUB;
          break;
        default:
          nType = NodeType.CHILD;
      }
    }

    // Convert children recursively
    List<Node> children = null;
    if (nodeModel.getChildren() != null) {
      children = new ArrayList<>();
      for (com.descope.utils.model.rebac.NodeModel child : nodeModel.getChildren()) {
        children.add(convertNodeModelToSdkNode(child));
      }
    }

    // Convert expression
    NodeExpression expression = null;
    if (nodeModel.getExpression() != null) {
      expression = convertNodeExpressionModelToSdk(nodeModel.getExpression());
    }

    return new Node(nType, children, expression);
  }

  /**
   * Converts a NodeExpressionModel to SDK NodeExpression.
   *
   * @param exprModel The expression model to convert
   * @return The SDK NodeExpression
   */
  private NodeExpression convertNodeExpressionModelToSdk(
      com.descope.utils.model.rebac.NodeExpressionModel exprModel) {
    if (exprModel == null) {
      return null;
    }

    // Convert expression type
    NodeExpressionType neType = null;
    if (exprModel.getNeType() != null) {
      switch (exprModel.getNeType().toLowerCase()) {
        case "self":
          neType = NodeExpressionType.SELF;
          break;
        case "targetset":
        case "target_set":
          neType = NodeExpressionType.TARGET_SET;
          break;
        case "relationleft":
        case "relation_left":
          neType = NodeExpressionType.RELATION_LEFT;
          break;
        case "relationright":
        case "relation_right":
          neType = NodeExpressionType.RELATION_RIGHT;
          break;
        default:
          neType = NodeExpressionType.SELF;
      }
    }

    return new NodeExpression(
        neType,
        exprModel.getRelationDefinition(),
        exprModel.getRelationDefinitionNamespace(),
        exprModel.getTargetRelationDefinition(),
        exprModel.getTargetRelationDefinitionNamespace());
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
        relationDefs.add(new RelationDefinitionModel(relDef.getName(), targetNamespaces, null));
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

  /**
   * Creates one or more FGA relation tuples.
   *
   * <p>This method creates authorization relationships between targets and resources.
   *
   * @param config The Descope configuration
   * @param tuples The list of relation tuples to create
   * @return OperationResult containing the created relations
   */
  public OperationResult<List<RelationTupleModel>> createRelations(
      DescopeConfig config, List<RelationTupleModel> tuples) {
    logger.info("Creating {} FGA relation tuple(s)", tuples.size());

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      // Convert models to SDK Relation objects
      List<com.descope.model.authz.Relation> relations = new ArrayList<>();
      for (RelationTupleModel tuple : tuples) {
        com.descope.model.authz.Relation relation = new com.descope.model.authz.Relation();
        relation.setResource(tuple.getResource());
        relation.setRelationDefinition(tuple.getRelationDefinition());
        relation.setNamespace(tuple.getNamespace());
        relation.setTarget(tuple.getTarget());
        relations.add(relation);
      }

      // Create the relations
      sdkAuthzService.createRelations(relations);

      logger.info("Successfully created {} relation tuple(s)", tuples.size());
      return OperationResult.created(
          tuples, "Created " + tuples.size() + " relation tuple(s) successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("create FGA relations", e);
    }
  }

  /**
   * Deletes one or more FGA relation tuples.
   *
   * @param config The Descope configuration
   * @param tuples The list of relation tuples to delete
   * @return OperationResult indicating success or failure
   */
  public OperationResult<String> deleteRelations(
      DescopeConfig config, List<RelationTupleModel> tuples) {
    logger.info("Deleting {} FGA relation tuple(s)", tuples.size());

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      // Convert models to SDK Relation objects
      List<com.descope.model.authz.Relation> relations = new ArrayList<>();
      for (RelationTupleModel tuple : tuples) {
        com.descope.model.authz.Relation relation = new com.descope.model.authz.Relation();
        relation.setResource(tuple.getResource());
        relation.setRelationDefinition(tuple.getRelationDefinition());
        relation.setNamespace(tuple.getNamespace());
        relation.setTarget(tuple.getTarget());
        relations.add(relation);
      }

      // Delete the relations
      sdkAuthzService.deleteRelations(relations);

      logger.info("Successfully deleted {} relation tuple(s)", tuples.size());
      return OperationResult.created("", "Deleted " + tuples.size() + " relation tuple(s)");

    } catch (DescopeException e) {
      throw descopeService.wrapException("delete FGA relations", e);
    }
  }

  /**
   * Checks if specific FGA relation tuples exist.
   *
   * @param config The Descope configuration
   * @param queries The list of relation queries to check
   * @return OperationResult containing a map of query results
   */
  public OperationResult<List<com.descope.model.authz.RelationQuery>> checkRelations(
      DescopeConfig config, List<RelationQueryModel> queries) {
    logger.info("Checking {} FGA relation query(ies)", queries.size());

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      // Convert models to SDK RelationQuery objects
      List<com.descope.model.authz.RelationQuery> relationQueries = new ArrayList<>();
      for (RelationQueryModel query : queries) {
        com.descope.model.authz.RelationQuery relationQuery =
            new com.descope.model.authz.RelationQuery();
        relationQuery.setResource(query.getResource());
        relationQuery.setRelationDefinition(query.getRelationDefinition());
        relationQuery.setNamespace(query.getNamespace());
        relationQuery.setTarget(query.getTarget());
        relationQueries.add(relationQuery);
      }

      // Check the relations
      List<com.descope.model.authz.RelationQuery> results =
          sdkAuthzService.hasRelations(relationQueries);

      logger.info("Successfully checked {} relation query(ies)", queries.size());
      return OperationResult.success(results, "Checked " + queries.size() + " relation(s)");

    } catch (DescopeException e) {
      throw descopeService.wrapException("check FGA relations", e);
    }
  }

  /**
   * Queries who can access a specific resource.
   *
   * @param config The Descope configuration
   * @param resource The resource identifier
   * @param relationDefinition The relation definition name
   * @param namespace The namespace
   * @return OperationResult containing the list of targets that can access the resource
   */
  public OperationResult<List<String>> whoCanAccess(
      DescopeConfig config, String resource, String relationDefinition, String namespace) {
    logger.info(
        "Querying who can access resource: {} with relation: {} in namespace: {}",
        resource,
        relationDefinition,
        namespace);

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      List<String> targets = sdkAuthzService.whoCanAccess(resource, relationDefinition, namespace);

      logger.info("Found {} target(s) that can access the resource", targets.size());
      return OperationResult.success(targets, "Found " + targets.size() + " target(s) with access");

    } catch (DescopeException e) {
      throw descopeService.wrapException("query who can access", e);
    }
  }

  /**
   * Gets all relations for a specific resource.
   *
   * @param config The Descope configuration
   * @param resource The resource identifier
   * @return OperationResult containing the list of relation tuples for the resource
   */
  public OperationResult<List<RelationTupleModel>> resourceRelations(
      DescopeConfig config, String resource) {
    logger.info("Querying relations for resource: {}", resource);

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      List<com.descope.model.authz.Relation> relations =
          sdkAuthzService.resourceRelations(resource);

      // Convert SDK relations to models
      List<RelationTupleModel> tuples = new ArrayList<>();
      for (com.descope.model.authz.Relation relation : relations) {
        tuples.add(
            new RelationTupleModel(
                relation.getResource(),
                relation.getRelationDefinition(),
                relation.getNamespace(),
                relation.getTarget()));
      }

      logger.info("Found {} relation(s) for resource", tuples.size());
      return OperationResult.success(tuples, "Found " + tuples.size() + " relation(s)");

    } catch (DescopeException e) {
      throw descopeService.wrapException("query resource relations", e);
    }
  }

  /**
   * Queries what resources a specific target can access.
   *
   * @param config The Descope configuration
   * @param target The target/subject identifier
   * @return OperationResult containing the list of relation tuples showing what the target can
   *     access
   */
  public OperationResult<List<RelationTupleModel>> whatCanTargetAccess(
      DescopeConfig config, String target) {
    logger.info("Querying what target can access: {}", target);

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.AuthzService sdkAuthzService =
          client.getManagementServices().getAuthzService();

      List<com.descope.model.authz.Relation> relations =
          sdkAuthzService.whatCanTargetAccess(target);

      // Convert SDK relations to models
      List<RelationTupleModel> tuples = new ArrayList<>();
      for (com.descope.model.authz.Relation relation : relations) {
        tuples.add(
            new RelationTupleModel(
                relation.getResource(),
                relation.getRelationDefinition(),
                relation.getNamespace(),
                relation.getTarget()));
      }

      logger.info("Found {} relation(s) for target", tuples.size());
      return OperationResult.success(tuples, "Found " + tuples.size() + " relation(s)");

    } catch (DescopeException e) {
      throw descopeService.wrapException("query target access", e);
    }
  }
}
