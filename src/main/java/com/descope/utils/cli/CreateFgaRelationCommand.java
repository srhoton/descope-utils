package com.descope.utils.cli;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.RelationBatchModel;
import com.descope.utils.model.fga.RelationTupleModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.AuthzService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to create FGA relation tuples.
 *
 * <p>This command creates authorization relationships between targets and resources. Relations can
 * be specified either through command-line options or by loading from a JSON file.
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
      description = "Resource identifier (e.g., 'document:report-123')")
  private String resource;

  @Option(
      names = {"--relation"},
      description = "Relation definition name (e.g., 'owner', 'viewer')")
  private String relationDefinition;

  @Option(
      names = {"-n", "--namespace"},
      description = "Namespace for the resource")
  private String namespace;

  @Option(
      names = {"-t", "--target"},
      description = "Target/subject identifier (e.g., 'user:alice@example.com')")
  private String target;

  @Option(
      names = {"-f", "--file"},
      description = "Path to JSON file containing relation tuples")
  private String file;

  @Inject private ConfigurationService configService;
  @Inject private AuthzService authzService;
  @Inject private OutputFormatter outputFormatter;
  @Inject private ObjectMapper objectMapper;

  @Override
  public void run() {
    try {
      // Validate input
      if (file == null
          && (resource == null
              || relationDefinition == null
              || namespace == null
              || target == null)) {
        System.err.println(
            "Error: Either provide --file or all of --resource, --relation, --namespace, and --target");
        System.exit(1);
        return;
      }

      if (file != null
          && (resource != null
              || relationDefinition != null
              || namespace != null
              || target != null)) {
        System.err.println("Error: Cannot specify both --file and individual relation options");
        System.exit(1);
        return;
      }

      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      List<RelationTupleModel> tuples;

      if (file != null) {
        // Load relations from file
        logger.info("Loading relation tuples from file: {}", file);
        File jsonFile = new File(file);
        if (!jsonFile.exists()) {
          System.err.println("Error: File not found: " + file);
          System.exit(1);
          return;
        }
        RelationBatchModel batch = objectMapper.readValue(jsonFile, RelationBatchModel.class);
        tuples = batch.getRelations();
        logger.info("Loaded {} relation tuple(s) from file", tuples.size());
      } else {
        // Create single relation from command-line options
        logger.info("Creating single relation tuple");
        RelationTupleModel tuple =
            new RelationTupleModel(resource, relationDefinition, namespace, target);
        tuples = Collections.singletonList(tuple);
      }

      // Create relations
      OperationResult<List<RelationTupleModel>> result =
          authzService.createRelations(config, tuples);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to create FGA relation(s)", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
