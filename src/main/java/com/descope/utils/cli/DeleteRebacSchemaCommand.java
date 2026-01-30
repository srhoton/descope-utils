package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.AuthzService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

/**
 * Command to delete the current ReBAC authorization schema.
 *
 * <p>This command removes the entire ReBAC schema including all namespaces, relation definitions,
 * and relations.
 */
@Command(
    name = "delete-rebac-schema",
    description = "Delete the current ReBAC authorization schema",
    mixinStandardHelpOptions = true)
public class DeleteRebacSchemaCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(DeleteRebacSchemaCommand.class);

  @Mixin private GlobalOptions globalOptions;

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

      logger.info("Deleting ReBAC schema");

      // Delete schema
      OperationResult<String> result = authzService.deleteSchema(config);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to delete ReBAC schema", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
