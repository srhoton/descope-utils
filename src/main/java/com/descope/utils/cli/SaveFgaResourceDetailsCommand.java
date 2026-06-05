package com.descope.utils.cli;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.FgaResourceDetailsBatchModel;
import com.descope.utils.model.fga.FgaResourceDetailsModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.FgaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to save display metadata for FGA resources.
 *
 * <p>Stores human-readable display names for resources, used by UI components. A single resource
 * can be specified via CLI options, or multiple via a JSON file:
 *
 * <pre>{@code
 * {
 *   "details": [
 *     {"resourceId": "doc1", "resourceType": "document", "displayName": "My Document"}
 *   ]
 * }
 * }</pre>
 */
@Command(
    name = "save-fga-resource-details",
    description = "Save display metadata (display name) for FGA resources",
    mixinStandardHelpOptions = true)
public class SaveFgaResourceDetailsCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(SaveFgaResourceDetailsCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"--resource-id"},
      description = "Resource identifier (e.g., 'doc1')")
  private String resourceId;

  @Option(
      names = {"--resource-type"},
      description = "Resource type (e.g., 'document')")
  private String resourceType;

  @Option(
      names = {"--display-name"},
      description = "Human-readable display name for the resource")
  private String displayName;

  @Option(
      names = {"-f", "--file"},
      description =
          "Path to JSON file containing resource details"
              + " ({\"details\":[{\"resourceId\":\"...\",\"resourceType\":\"...\","
              + "\"displayName\":\"...\"}]})")
  private String file;

  @Inject private ConfigurationService configService;
  @Inject private FgaService fgaService;
  @Inject private OutputFormatter outputFormatter;
  @Inject private ObjectMapper objectMapper;

  @Override
  public void run() {
    try {
      if (file == null && (resourceId == null || resourceType == null || displayName == null)) {
        System.err.println(
            "Error: Either provide --file or all of --resource-id, --resource-type, --display-name");
        System.exit(1);
        return;
      }

      if (file != null && (resourceId != null || resourceType != null || displayName != null)) {
        System.err.println(
            "Error: Cannot specify both --file and individual resource detail options");
        System.exit(1);
        return;
      }

      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      List<FgaResourceDetailsModel> details;

      if (file != null) {
        logger.info("Loading FGA resource details from file: {}", file);
        File jsonFile = new File(file);
        if (!jsonFile.exists()) {
          System.err.println("Error: File not found: " + file);
          System.exit(1);
          return;
        }
        FgaResourceDetailsBatchModel batch =
            objectMapper.readValue(jsonFile, FgaResourceDetailsBatchModel.class);
        details = batch.getDetails();
        logger.info("Loaded {} FGA resource detail(s) from file", details.size());
      } else {
        logger.info("Saving single FGA resource detail");
        details =
            Collections.singletonList(
                new FgaResourceDetailsModel(resourceId, resourceType, displayName));
      }

      OperationResult<String> result = fgaService.saveResourceDetails(config, details);

      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to save FGA resource details", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
