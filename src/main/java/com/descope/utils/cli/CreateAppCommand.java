package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.Application;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.ApplicationService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to create a Descope application.
 *
 * <p>Creates a new OIDC/SAML application in Descope with the specified name and optional
 * description.
 */
@Command(
    name = "create-app",
    description = "Create a new Descope application",
    mixinStandardHelpOptions = true)
public class CreateAppCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CreateAppCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "Application name")
  private String name;

  @Option(
      names = {"-d", "--description"},
      description = "Application description")
  private String description;

  @Inject private ConfigurationService configService;
  @Inject private ApplicationService applicationService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      logger.info("Creating application: {}", name);

      // Create the application
      OperationResult<Application> result =
          applicationService.createApplication(config, name, description);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to create application", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
