package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.FederatedAppType;
import com.descope.utils.model.FederatedApplication;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.FederatedApplicationService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to create a Descope federated application.
 *
 * <p>Creates a new OIDC or SAML federated application in Descope with the specified name and
 * optional configuration parameters.
 */
@Command(
    name = "create-federated-app",
    description = "Create a new Descope federated application (OIDC or SAML)",
    mixinStandardHelpOptions = true)
public class CreateFederatedAppCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CreateFederatedAppCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "Federated application name")
  private String name;

  @Option(
      names = {"-t", "--type"},
      description = "Federated application type: oidc or saml (default: oidc)",
      defaultValue = "oidc")
  private String type;

  @Option(
      names = {"-d", "--description"},
      description = "Federated application description")
  private String description;

  @Option(
      names = {"-l", "--login-page-url"},
      description = "Login page URL for the federated application")
  private String loginPageUrl;

  @Inject private ConfigurationService configService;
  @Inject private FederatedApplicationService federatedApplicationService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      // Parse and validate federated app type
      FederatedAppType federatedAppType;
      try {
        federatedAppType = FederatedAppType.fromString(type);
      } catch (IllegalArgumentException e) {
        logger.error("Invalid federated application type: {}", type);
        System.err.println("Error: " + e.getMessage());
        System.exit(1);
        return;
      }

      logger.info("Creating {} federated application: {}", federatedAppType, name);

      // Create the federated application
      OperationResult<FederatedApplication> result =
          federatedApplicationService.createFederatedApplication(
              config, name, description, federatedAppType, loginPageUrl);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to create federated application", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
