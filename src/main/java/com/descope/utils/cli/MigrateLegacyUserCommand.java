package com.descope.utils.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.MigratedUser;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.MigrationService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to migrate a legacy user to Descope.
 *
 * <p>Creates a user in the specified tenant with their existing bcrypt password hash preserved,
 * allowing them to authenticate with their existing password.
 */
@Command(
    name = "migrate-legacy-user",
    description = "Migrate a legacy user to Descope with their existing bcrypt password",
    mixinStandardHelpOptions = true)
public class MigrateLegacyUserCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(MigrateLegacyUserCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"-e", "--email"},
      description = "User's email address (also used as login ID)",
      required = true)
  private String email;

  @Option(
      names = {"-f", "--first-name"},
      description = "User's first name",
      required = true)
  private String firstName;

  @Option(
      names = {"-l", "--last-name"},
      description = "User's last name",
      required = true)
  private String lastName;

  @Option(
      names = {"-t", "--tenant"},
      description = "Tenant ID to associate the user with",
      required = true)
  private String tenantId;

  @Option(
      names = {"-r", "--roles"},
      description = "Comma-separated list of role names to assign",
      split = ",")
  private List<String> roles;

  @Option(
      names = {"-b", "--bcrypt-hash"},
      description = "User's existing password in bcrypt format (e.g., $2a$10$...)",
      required = true)
  private String bcryptHash;

  @Inject private ConfigurationService configService;
  @Inject private MigrationService migrationService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      logger.info("Migrating legacy user: {} to tenant: {}", email, tenantId);

      // Migrate the user
      OperationResult<MigratedUser> result =
          migrationService.migrateLegacyUser(
              config, email, firstName, lastName, tenantId, roles, bcryptHash);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to migrate user", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
