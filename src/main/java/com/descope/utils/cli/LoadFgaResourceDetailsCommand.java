package com.descope.utils.cli;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.FgaResourceDetailsModel;
import com.descope.utils.model.fga.FgaResourceIdentifierBatchModel;
import com.descope.utils.model.fga.FgaResourceIdentifierModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.FgaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to load display metadata for FGA resources.
 *
 * <p>Retrieves human-readable display names for the specified resources. A single resource can be
 * specified via CLI options, or multiple via a JSON file:
 *
 * <pre>{@code
 * {
 *   "identifiers": [
 *     {"resourceId": "doc1", "resourceType": "document"}
 *   ]
 * }
 * }</pre>
 */
@Command(
    name = "load-fga-resource-details",
    description = "Load display metadata (display name) for FGA resources",
    mixinStandardHelpOptions = true)
public class LoadFgaResourceDetailsCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(LoadFgaResourceDetailsCommand.class);

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
      names = {"-f", "--file"},
      description =
          "Path to JSON file containing resource identifiers"
              + " ({\"identifiers\":[{\"resourceId\":\"...\",\"resourceType\":\"...\"}]})")
  private String file;

  @Inject private ConfigurationService configService;
  @Inject private FgaService fgaService;
  @Inject private OutputFormatter outputFormatter;
  @Inject private ObjectMapper objectMapper;

  @Override
  public void run() {
    try {
      if (file == null && (resourceId == null || resourceType == null)) {
        System.err.println(
            "Error: Either provide --file or both --resource-id and --resource-type");
        System.exit(1);
        return;
      }

      if (file != null && (resourceId != null || resourceType != null)) {
        System.err.println(
            "Error: Cannot specify both --file and individual resource identifier options");
        System.exit(1);
        return;
      }

      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      List<FgaResourceIdentifierModel> identifiers;

      if (file != null) {
        logger.info("Loading FGA resource identifiers from file: {}", file);
        File jsonFile = new File(file);
        if (!jsonFile.exists()) {
          System.err.println("Error: File not found: " + file);
          System.exit(1);
          return;
        }
        FgaResourceIdentifierBatchModel batch =
            objectMapper.readValue(jsonFile, FgaResourceIdentifierBatchModel.class);
        identifiers = batch.getIdentifiers();
        logger.info("Loaded {} FGA resource identifier(s) from file", identifiers.size());
      } else {
        logger.info("Loading FGA resource details for single resource");
        identifiers =
            Collections.singletonList(new FgaResourceIdentifierModel(resourceId, resourceType));
      }

      OperationResult<List<FgaResourceDetailsModel>> result =
          fgaService.loadResourceDetails(config, identifiers);

      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to load FGA resource details", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
