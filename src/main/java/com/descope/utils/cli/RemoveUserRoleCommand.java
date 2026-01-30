package com.descope.utils.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.UserService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to remove roles from a user.
 *
 * <p>Removes one or more roles from a user at project level or within a specific tenant.
 */
@Command(
    name = "remove-user-role",
    description = "Remove roles from a user",
    mixinStandardHelpOptions = true)
public class RemoveUserRoleCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(RemoveUserRoleCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "User login ID (email, phone, or username)")
  private String loginId;

  @Option(
      names = {"-r", "--roles"},
      description = "Comma-separated list of role names to remove",
      required = true,
      split = ",")
  private List<String> roles;

  @Option(
      names = {"-t", "--tenant"},
      description = "Tenant ID for tenant-specific roles (omit for project-level)")
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

      String context = tenantId != null ? " in tenant: " + tenantId : " (project-level)";
      logger.info("Removing roles {} from user: {}{}", roles, loginId, context);

      // Remove the roles
      OperationResult<Void> result;
      if (tenantId != null && !tenantId.isEmpty()) {
        result = userService.removeTenantRoles(config, loginId, tenantId, roles);
      } else {
        result = userService.removeRoles(config, loginId, roles);
      }

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to remove roles from user", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
