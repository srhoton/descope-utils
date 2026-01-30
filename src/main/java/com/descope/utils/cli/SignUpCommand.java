package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.AuthenticationResult;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.OutputFormat;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.AuthenticationService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to sign up a new user with password authentication.
 *
 * <p>Creates a new user with password and returns session and refresh JWTs.
 */
@Command(
    name = "signup",
    description = "Sign up a new user with password authentication",
    mixinStandardHelpOptions = true)
public class SignUpCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(SignUpCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "User login ID (email, phone, or username)")
  private String loginId;

  @Option(
      names = {"-w", "--password"},
      description = "User password (must meet configured requirements)",
      required = true,
      interactive = true,
      arity = "0..1")
  private String password;

  @Option(
      names = {"-n", "--name"},
      description = "User's display name")
  private String name;

  @Option(
      names = {"-e", "--email"},
      description = "User's email address (defaults to loginId if it's an email)")
  private String email;

  @Option(
      names = {"--phone"},
      description = "User's phone number")
  private String phone;

  @Option(
      names = {"--show-tokens"},
      description = "Display the full JWT tokens in output (default: false)",
      defaultValue = "false")
  private boolean showTokens;

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

      logger.info("Signing up user: {}", loginId);

      // Sign up the user
      OperationResult<AuthenticationResult> result =
          authenticationService.signUpWithPassword(config, loginId, password, name, email, phone);

      // Format and print the result
      if (globalOptions.getOutputFormat() == OutputFormat.JSON || showTokens) {
        // For JSON output or when explicitly requested, include full tokens
        String output = outputFormatter.format(result, globalOptions.getOutputFormat());
        System.out.println(output);
      } else {
        // For text output, show a summary with the session JWT
        printTextResult(result);
      }

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to sign up user", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }

  private void printTextResult(OperationResult<AuthenticationResult> result) {
    AuthenticationResult auth = result.getData();
    StringBuilder sb = new StringBuilder();

    sb.append("\u2713 Sign-up successful\n");
    sb.append("\u2500".repeat(60)).append("\n");
    sb.append("User Details:\n");
    sb.append("  Login ID:    ").append(auth.getLoginId()).append("\n");
    sb.append("  User ID:     ").append(auth.getUserId()).append("\n");
    sb.append("  Expires At:  ").append(auth.getExpiresAt()).append("\n");
    sb.append("\n");

    if (showTokens) {
      sb.append("Session JWT:\n");
      sb.append(auth.getSessionJwt()).append("\n");
      if (auth.getRefreshJwt() != null) {
        sb.append("\nRefresh JWT:\n");
        sb.append(auth.getRefreshJwt()).append("\n");
      }
    } else {
      sb.append("Session JWT:\n");
      sb.append(auth.getSessionJwt()).append("\n");
      sb.append("\n(Use --show-tokens to display refresh token as well)\n");
    }

    System.out.println(sb);
  }
}
