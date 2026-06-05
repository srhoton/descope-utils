package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.OutputFormat;
import com.descope.utils.model.fga.FgaSchemaModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.FgaService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

/**
 * Command to load the current FGA schema.
 *
 * <p>Retrieves the AuthZ 1.0 DSL schema currently configured for the project. In TEXT output mode
 * the DSL is printed directly; in JSON mode the schema is wrapped in a JSON object.
 */
@Command(
    name = "load-fga-schema",
    description = "Load the current FGA schema (AuthZ 1.0 DSL format)",
    mixinStandardHelpOptions = true)
public class LoadFgaSchemaCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(LoadFgaSchemaCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Inject private ConfigurationService configService;
  @Inject private FgaService fgaService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      OperationResult<FgaSchemaModel> result = fgaService.loadSchema(config);

      if (result.isSuccess() && result.getData() != null) {
        OutputFormat outputFormat = globalOptions.getOutputFormat();
        if (OutputFormat.TEXT.equals(outputFormat)) {
          System.out.println(result.getData().getDsl());
        } else {
          String output = outputFormatter.format(result, outputFormat);
          System.out.println(output);
        }
      } else {
        String output = outputFormatter.format(result, globalOptions.getOutputFormat());
        System.out.println(output);
      }

      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to load FGA schema", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
