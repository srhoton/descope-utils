package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.User;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.UserService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to create a Descope user.
 *
 * <p>Creates a new user in Descope with the specified login ID and optionally associates it with a
 * tenant.
 */
@Command(
    name = "create-user",
    description = "Create a new Descope user",
    mixinStandardHelpOptions = true)
public class CreateUserCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CreateUserCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "User login ID")
  private String loginId;

  @Option(
      names = {"-e", "--email"},
      description = "User email address")
  private String email;

  @Option(
      names = {"-t", "--tenant-id"},
      description = "Tenant ID to associate the user with",
      required = true)
  private String tenantId;

  @Inject private ConfigurationService configService;
  @Inject private UserService userService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      logger.info("Creating user: {} in tenant: {}", loginId, tenantId);

      // Create the user
      OperationResult<User> result = userService.createUser(loginId, email, tenantId);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to create user", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
