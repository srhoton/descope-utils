package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.AuthenticationService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to set a password for an existing user.
 *
 * <p>Allows administrators to set either an active or temporary password for a user. Active
 * passwords can be used immediately, while temporary passwords require the user to change their
 * password on the next login.
 */
@Command(
    name = "set-password",
    description = "Set a password for an existing user",
    mixinStandardHelpOptions = true)
public class SetPasswordCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(SetPasswordCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "User login ID (email, phone, or username)")
  private String loginId;

  @Option(
      names = {"-w", "--password"},
      description = "New password to set (must meet configured requirements)",
      required = true,
      interactive = true,
      arity = "0..1")
  private String password;

  @Option(
      names = {"-t", "--temporary"},
      description = "Set as temporary password (user must change on next login)",
      defaultValue = "false")
  private boolean temporary;

  @Inject private ConfigurationService configService;
  @Inject private AuthenticationService authenticationService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      String passwordType = temporary ? "temporary" : "active";
      logger.info("Setting {} password for user: {}", passwordType, loginId);

      // Set the password
      OperationResult<Void> result;
      if (temporary) {
        result = authenticationService.setTemporaryPassword(config, loginId, password);
      } else {
        result = authenticationService.setActivePassword(config, loginId, password);
      }

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to set password for user", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
