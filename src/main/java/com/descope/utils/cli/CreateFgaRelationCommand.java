package com.descope.utils.cli;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.FgaRelationBatchModel;
import com.descope.utils.model.fga.FgaRelationModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.FgaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to create FGA relation tuples.
 *
 * <p>Creates authorization relationships between targets and resources using the Descope
 * FGAService. Relations can be specified via CLI options (single relation) or a JSON file (batch).
 */
@Command(
    name = "create-fga-relation",
    description = "Create FGA relation tuple(s) between targets and resources",
    mixinStandardHelpOptions = true)
public class CreateFgaRelationCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CreateFgaRelationCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"-r", "--resource"},
      description = "Resource identifier (e.g., 'doc1')")
  private String resource;

  @Option(
      names = {"--resource-type"},
      description = "Resource type (e.g., 'document')")
  private String resourceType;

  @Option(
      names = {"--relation"},
      description = "Relation name (e.g., 'owner', 'viewer')")
  private String relation;

  @Option(
      names = {"-t", "--target"},
      description = "Target identifier (e.g., 'user1')")
  private String target;

  @Option(
      names = {"--target-type"},
      description = "Target type (e.g., 'user', 'organization')")
  private String targetType;

  @Option(
      names = {"-f", "--file"},
      description =
          "Path to JSON file containing relation tuples"
              + " ({\"relations\":[{\"resource\":\"...\",\"resourceType\":\"...\","
              + "\"relation\":\"...\",\"target\":\"...\",\"targetType\":\"...\"}]})")
  private String file;

  @Inject private ConfigurationService configService;
  @Inject private FgaService fgaService;
  @Inject private OutputFormatter outputFormatter;
  @Inject private ObjectMapper objectMapper;

  @Override
  public void run() {
    try {
      if (file == null
          && (resource == null
              || resourceType == null
              || relation == null
              || target == null
              || targetType == null)) {
        System.err.println(
            "Error: Either provide --file or all of"
                + " --resource, --resource-type, --relation, --target, --target-type");
        System.exit(1);
        return;
      }

      if (file != null
          && (resource != null
              || resourceType != null
              || relation != null
              || target != null
              || targetType != null)) {
        System.err.println("Error: Cannot specify both --file and individual relation options");
        System.exit(1);
        return;
      }

      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      List<FgaRelationModel> tuples;

      if (file != null) {
        logger.info("Loading FGA relation tuples from file: {}", file);
        File jsonFile = new File(file);
        if (!jsonFile.exists()) {
          System.err.println("Error: File not found: " + file);
          System.exit(1);
          return;
        }
        FgaRelationBatchModel batch = objectMapper.readValue(jsonFile, FgaRelationBatchModel.class);
        tuples = batch.getRelations();
        logger.info("Loaded {} FGA relation tuple(s) from file", tuples.size());
      } else {
        logger.info("Creating single FGA relation tuple");
        tuples =
            Collections.singletonList(
                new FgaRelationModel(resource, resourceType, relation, target, targetType));
      }

      OperationResult<List<FgaRelationModel>> result = fgaService.createRelations(config, tuples);

      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to create FGA relation(s)", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
