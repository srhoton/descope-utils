package com.descope.utils.cli;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.RelationQueryModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.AuthzService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to check if FGA relation tuples exist.
 *
 * <p>This command checks whether specific authorization relationships exist between targets and
 * resources.
 */
@Command(
    name = "check-fga-relation",
    description = "Check if FGA relation tuple(s) exist",
    mixinStandardHelpOptions = true)
public class CheckFgaRelationCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CheckFgaRelationCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"-r", "--resource"},
      description = "Resource identifier (e.g., 'document:report-123')",
      required = true)
  private String resource;

  @Option(
      names = {"--relation"},
      description = "Relation definition name (e.g., 'owner', 'viewer')",
      required = true)
  private String relationDefinition;

  @Option(
      names = {"-n", "--namespace"},
      description = "Namespace for the resource",
      required = true)
  private String namespace;

  @Option(
      names = {"-t", "--target"},
      description = "Target/subject identifier (e.g., 'user:alice@example.com')",
      required = true)
  private String target;

  @Inject private ConfigurationService configService;
  @Inject private AuthzService authzService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      logger.info(
          "Checking relation: target={}, resource={}, relation={}, namespace={}",
          target,
          resource,
          relationDefinition,
          namespace);

      // Create query
      RelationQueryModel query =
          new RelationQueryModel(resource, relationDefinition, namespace, target);
      List<RelationQueryModel> queries = Collections.singletonList(query);

      // Check relations
      OperationResult<List<com.descope.model.authz.RelationQuery>> result =
          authzService.checkRelations(config, queries);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to check FGA relation", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
