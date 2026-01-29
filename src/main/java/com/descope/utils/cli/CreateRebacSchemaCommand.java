package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.rebac.SchemaModel;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.AuthzService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to create or update a ReBAC authorization schema.
 *
 * <p>This command creates or updates a ReBAC (Relationship-Based Access Control) schema from a JSON
 * file. The schema defines namespaces and relation definitions for authorization.
 */
@Command(
    name = "create-rebac-schema",
    description = "Create or update a ReBAC authorization schema from a JSON file",
    mixinStandardHelpOptions = true)
public class CreateRebacSchemaCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CreateRebacSchemaCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"-f", "--file"},
      description = "Path to the JSON schema file",
      required = true)
  private String schemaFile;

  @Option(
      names = {"-u", "--upgrade"},
      description = "Upgrade existing schema (default: true)",
      defaultValue = "true")
  private boolean upgrade;

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

      logger.info("Creating/updating ReBAC schema from file: {}", schemaFile);

      // Create schema
      OperationResult<SchemaModel> result = authzService.createSchema(config, schemaFile, upgrade);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to create ReBAC schema", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
