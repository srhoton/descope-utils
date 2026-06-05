package com.descope.utils.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.exception.DescopeException;
import com.descope.model.fga.FGACheckInfo;
import com.descope.model.fga.FGACheckResult;
import com.descope.model.fga.FGARelation;
import com.descope.model.fga.FGAResourceDetails;
import com.descope.model.fga.FGAResourceIdentifier;
import com.descope.model.fga.FGASchema;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.FgaCheckResultModel;
import com.descope.utils.model.fga.FgaRelationModel;
import com.descope.utils.model.fga.FgaResourceDetailsModel;
import com.descope.utils.model.fga.FgaResourceIdentifierModel;
import com.descope.utils.model.fga.FgaSchemaModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing Fine-Grained Authorization (FGA) using the Descope FGAService API.
 *
 * <p>Covers schema management (AuthZ 1.0 DSL), relation tuples, authorization checks, and resource
 * detail metadata.
 */
@ApplicationScoped
public class FgaService {

  private static final Logger logger = LoggerFactory.getLogger(FgaService.class);

  private final DescopeService descopeService;

  /**
   * Creates a new FgaService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public FgaService(DescopeService descopeService) {
    this.descopeService = descopeService;
  }

  /**
   * Saves (creates or updates) an FGA schema using the AuthZ 1.0 DSL format.
   *
   * @param config The Descope configuration
   * @param dsl The AuthZ 1.0 DSL schema definition
   * @return OperationResult indicating success or failure
   */
  public OperationResult<String> saveSchema(DescopeConfig config, String dsl) {
    logger.info("Saving FGA schema");

    try {
      DescopeClient client = descopeService.createClient(config);
      client.getManagementServices().getFgaService().saveSchema(new FGASchema(dsl));

      logger.info("Successfully saved FGA schema");
      return OperationResult.created("", "FGA schema saved successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("save FGA schema", e);
    }
  }

  /**
   * Loads the current FGA schema for the project.
   *
   * @param config The Descope configuration
   * @return OperationResult containing the current schema DSL
   */
  public OperationResult<FgaSchemaModel> loadSchema(DescopeConfig config) {
    logger.info("Loading FGA schema");

    try {
      DescopeClient client = descopeService.createClient(config);
      FGASchema schema = client.getManagementServices().getFgaService().loadSchema();

      if (schema == null || schema.getDsl() == null || schema.getDsl().isBlank()) {
        logger.info("No FGA schema found");
        return OperationResult.failure("No FGA schema exists");
      }

      FgaSchemaModel model = new FgaSchemaModel(schema.getDsl());
      logger.info("Successfully loaded FGA schema");
      return OperationResult.success(model, "FGA schema loaded successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("load FGA schema", e);
    }
  }

  /**
   * Creates one or more FGA relations.
   *
   * @param config The Descope configuration
   * @param relations The list of relations to create
   * @return OperationResult containing the created relations
   */
  public OperationResult<List<FgaRelationModel>> createRelations(
      DescopeConfig config, List<FgaRelationModel> relations) {
    logger.info("Creating {} FGA relation(s)", relations.size());

    try {
      DescopeClient client = descopeService.createClient(config);
      client.getManagementServices().getFgaService().createRelations(toSdkRelations(relations));

      logger.info("Successfully created {} FGA relation(s)", relations.size());
      return OperationResult.created(
          relations, "Created " + relations.size() + " FGA relation(s) successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("create FGA relations", e);
    }
  }

  /**
   * Deletes one or more FGA relations.
   *
   * @param config The Descope configuration
   * @param relations The list of relations to delete
   * @return OperationResult indicating success or failure
   */
  public OperationResult<String> deleteRelations(
      DescopeConfig config, List<FgaRelationModel> relations) {
    logger.info("Deleting {} FGA relation(s)", relations.size());

    try {
      DescopeClient client = descopeService.createClient(config);
      client.getManagementServices().getFgaService().deleteRelations(toSdkRelations(relations));

      logger.info("Successfully deleted {} FGA relation(s)", relations.size());
      return OperationResult.created("", "Deleted " + relations.size() + " FGA relation(s)");

    } catch (DescopeException e) {
      throw descopeService.wrapException("delete FGA relations", e);
    }
  }

  /**
   * Checks whether specified FGA relations are satisfied.
   *
   * @param config The Descope configuration
   * @param relations The list of relations to check
   * @return OperationResult containing check results per relation
   */
  public OperationResult<List<FgaCheckResultModel>> checkRelations(
      DescopeConfig config, List<FgaRelationModel> relations) {
    logger.info("Checking {} FGA relation(s)", relations.size());

    try {
      DescopeClient client = descopeService.createClient(config);
      List<FGACheckResult> sdkResults =
          client.getManagementServices().getFgaService().check(toSdkRelations(relations));

      List<FgaCheckResultModel> results = new ArrayList<>();
      for (FGACheckResult r : sdkResults) {
        FGARelation rel = r.getRelation();
        FGACheckInfo info = r.getInfo();
        boolean direct = info != null && info.isDirect();
        results.add(
            new FgaCheckResultModel(
                r.isAllowed(),
                rel != null ? rel.getResource() : null,
                rel != null ? rel.getResourceType() : null,
                rel != null ? rel.getRelation() : null,
                rel != null ? rel.getTarget() : null,
                rel != null ? rel.getTargetType() : null,
                direct));
      }

      logger.info("Successfully checked {} FGA relation(s)", relations.size());
      return OperationResult.success(results, "Checked " + relations.size() + " relation(s)");

    } catch (DescopeException e) {
      throw descopeService.wrapException("check FGA relations", e);
    }
  }

  /**
   * Loads display metadata for the specified resources.
   *
   * @param config The Descope configuration
   * @param identifiers The list of resource identifiers to look up
   * @return OperationResult containing the resource details
   */
  public OperationResult<List<FgaResourceDetailsModel>> loadResourceDetails(
      DescopeConfig config, List<FgaResourceIdentifierModel> identifiers) {
    logger.info("Loading details for {} FGA resource(s)", identifiers.size());

    try {
      DescopeClient client = descopeService.createClient(config);

      List<FGAResourceIdentifier> sdkIds = new ArrayList<>();
      for (FgaResourceIdentifierModel id : identifiers) {
        sdkIds.add(new FGAResourceIdentifier(id.getResourceId(), id.getResourceType()));
      }

      List<FGAResourceDetails> sdkDetails =
          client.getManagementServices().getFgaService().loadResourcesDetails(sdkIds);

      List<FgaResourceDetailsModel> results = new ArrayList<>();
      for (FGAResourceDetails d : sdkDetails) {
        results.add(
            new FgaResourceDetailsModel(
                d.getResourceId(), d.getResourceType(), d.getDisplayName()));
      }

      logger.info("Successfully loaded details for {} FGA resource(s)", results.size());
      return OperationResult.success(
          results, "Loaded details for " + results.size() + " resource(s)");

    } catch (DescopeException e) {
      throw descopeService.wrapException("load FGA resource details", e);
    }
  }

  /**
   * Saves display metadata for the specified resources.
   *
   * @param config The Descope configuration
   * @param details The list of resource details to save
   * @return OperationResult indicating success or failure
   */
  public OperationResult<String> saveResourceDetails(
      DescopeConfig config, List<FgaResourceDetailsModel> details) {
    logger.info("Saving details for {} FGA resource(s)", details.size());

    try {
      DescopeClient client = descopeService.createClient(config);

      List<FGAResourceDetails> sdkDetails = new ArrayList<>();
      for (FgaResourceDetailsModel d : details) {
        sdkDetails.add(
            new FGAResourceDetails(d.getResourceId(), d.getResourceType(), d.getDisplayName()));
      }

      client.getManagementServices().getFgaService().saveResourcesDetails(sdkDetails);

      logger.info("Successfully saved details for {} FGA resource(s)", details.size());
      return OperationResult.created("", "Saved details for " + details.size() + " resource(s)");

    } catch (DescopeException e) {
      throw descopeService.wrapException("save FGA resource details", e);
    }
  }

  private List<FGARelation> toSdkRelations(List<FgaRelationModel> models) {
    List<FGARelation> sdkRelations = new ArrayList<>();
    for (FgaRelationModel m : models) {
      sdkRelations.add(
          new FGARelation(
              m.getResource(),
              m.getResourceType(),
              m.getRelation(),
              m.getTarget(),
              m.getTargetType()));
    }
    return sdkRelations;
  }
}
