package com.descope.utils.cli;

import java.io.File;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.FgaService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to save (create or update) an FGA schema.
 *
 * <p>Saves an AuthZ 1.0 DSL schema either from an inline string or a file. Example DSL:
 *
 * <pre>
 * model AuthZ 1.0
 * type user
 * type document
 *   relation owner: user
 *   permission can_edit: owner
 * </pre>
 */
@Command(
    name = "save-fga-schema",
    description = "Save (create or update) an FGA schema in AuthZ 1.0 DSL format",
    mixinStandardHelpOptions = true)
public class SaveFgaSchemaCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(SaveFgaSchemaCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"--dsl"},
      description = "AuthZ 1.0 DSL schema definition string")
  private String dsl;

  @Option(
      names = {"-f", "--file"},
      description = "Path to a file containing the AuthZ 1.0 DSL schema definition")
  private String file;

  @Inject private ConfigurationService configService;
  @Inject private FgaService fgaService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      if (dsl == null && file == null) {
        System.err.println("Error: Either --dsl or --file is required");
        System.exit(1);
        return;
      }

      if (dsl != null && file != null) {
        System.err.println("Error: Cannot specify both --dsl and --file");
        System.exit(1);
        return;
      }

      String schemaContent;
      if (file != null) {
        File schemaFile = new File(file);
        if (!schemaFile.exists()) {
          System.err.println("Error: File not found: " + file);
          System.exit(1);
          return;
        }
        logger.info("Reading FGA schema from file: {}", file);
        schemaContent = Files.readString(schemaFile.toPath());
      } else {
        schemaContent = dsl;
      }

      if (schemaContent.isBlank()) {
        System.err.println("Error: Schema DSL cannot be blank");
        System.exit(1);
        return;
      }

      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      OperationResult<String> result = fgaService.saveSchema(config, schemaContent);

      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to save FGA schema", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
