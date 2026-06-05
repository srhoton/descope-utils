package com.descope.utils.cli;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.FgaCheckResultModel;
import com.descope.utils.model.fga.FgaRelationModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.FgaService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to check if an FGA relation is satisfied.
 *
 * <p>Validates whether a specific authorization relationship exists between a target and a resource
 * using the Descope FGAService.
 */
@Command(
    name = "check-fga-relation",
    description = "Check if an FGA relation is satisfied between a target and a resource",
    mixinStandardHelpOptions = true)
public class CheckFgaRelationCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CheckFgaRelationCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"-r", "--resource"},
      description = "Resource identifier (e.g., 'doc1')",
      required = true)
  private String resource;

  @Option(
      names = {"--resource-type"},
      description = "Resource type (e.g., 'document')",
      required = true)
  private String resourceType;

  @Option(
      names = {"--relation"},
      description = "Relation name (e.g., 'owner', 'viewer')",
      required = true)
  private String relation;

  @Option(
      names = {"-t", "--target"},
      description = "Target identifier (e.g., 'user1')",
      required = true)
  private String target;

  @Option(
      names = {"--target-type"},
      description = "Target type (e.g., 'user', 'organization')",
      required = true)
  private String targetType;

  @Inject private ConfigurationService configService;
  @Inject private FgaService fgaService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      logger.info(
          "Checking FGA relation: resource={}/{}, relation={}, target={}/{}",
          resource,
          resourceType,
          relation,
          target,
          targetType);

      FgaRelationModel rel =
          new FgaRelationModel(resource, resourceType, relation, target, targetType);
      List<FgaRelationModel> relations = Collections.singletonList(rel);

      OperationResult<List<FgaCheckResultModel>> result =
          fgaService.checkRelations(config, relations);

      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to check FGA relation", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
