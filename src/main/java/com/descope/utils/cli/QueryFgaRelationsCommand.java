package com.descope.utils.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.RelationTupleModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.AuthzService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to query FGA relations.
 *
 * <p>This command provides three query modes: - who-can-access: Find all targets that can access a
 * resource - resource-relations: Get all relations for a specific resource - target-access: Find
 * all resources a target can access
 */
@Command(
    name = "query-fga-relations",
    description = "Query FGA relations with different modes",
    mixinStandardHelpOptions = true)
public class QueryFgaRelationsCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(QueryFgaRelationsCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"-m", "--mode"},
      description = "Query mode: who-can-access, resource-relations, or target-access",
      required = true)
  private String mode;

  @Option(
      names = {"-r", "--resource"},
      description = "Resource identifier (required for who-can-access and resource-relations)")
  private String resource;

  @Option(
      names = {"--relation"},
      description = "Relation definition name (required for who-can-access)")
  private String relationDefinition;

  @Option(
      names = {"-n", "--namespace"},
      description = "Namespace (required for who-can-access)")
  private String namespace;

  @Option(
      names = {"-t", "--target"},
      description = "Target/subject identifier (required for target-access)")
  private String target;

  @Inject private ConfigurationService configService;
  @Inject private AuthzService authzService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Validate mode
      if (!"who-can-access".equals(mode)
          && !"resource-relations".equals(mode)
          && !"target-access".equals(mode)) {
        System.err.println(
            "Error: Invalid mode. Must be one of: who-can-access, resource-relations, target-access");
        System.exit(1);
        return;
      }

      // Validate required options for each mode
      if ("who-can-access".equals(mode)) {
        if (resource == null || relationDefinition == null || namespace == null) {
          System.err.println(
              "Error: who-can-access mode requires --resource, --relation, and --namespace");
          System.exit(1);
          return;
        }
      } else if ("resource-relations".equals(mode)) {
        if (resource == null) {
          System.err.println("Error: resource-relations mode requires --resource");
          System.exit(1);
          return;
        }
      } else if ("target-access".equals(mode)) {
        if (target == null) {
          System.err.println("Error: target-access mode requires --target");
          System.exit(1);
          return;
        }
      }

      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      // Execute query based on mode
      if ("who-can-access".equals(mode)) {
        logger.info(
            "Querying who can access resource: {} with relation: {} in namespace: {}",
            resource,
            relationDefinition,
            namespace);
        OperationResult<List<String>> result =
            authzService.whoCanAccess(config, resource, relationDefinition, namespace);
        String output = outputFormatter.format(result, globalOptions.getOutputFormat());
        System.out.println(output);
        System.exit(result.isSuccess() ? 0 : 1);

      } else if ("resource-relations".equals(mode)) {
        logger.info("Querying relations for resource: {}", resource);
        OperationResult<List<RelationTupleModel>> result =
            authzService.resourceRelations(config, resource);
        String output = outputFormatter.format(result, globalOptions.getOutputFormat());
        System.out.println(output);
        System.exit(result.isSuccess() ? 0 : 1);

      } else if ("target-access".equals(mode)) {
        logger.info("Querying what target can access: {}", target);
        OperationResult<List<RelationTupleModel>> result =
            authzService.whatCanTargetAccess(config, target);
        String output = outputFormatter.format(result, globalOptions.getOutputFormat());
        System.out.println(output);
        System.exit(result.isSuccess() ? 0 : 1);
      }

    } catch (Exception e) {
      logger.error("Failed to query FGA relations", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
