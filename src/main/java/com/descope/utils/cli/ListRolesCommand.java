package com.descope.utils.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.OutputFormat;
import com.descope.utils.model.Role;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.RoleService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

/**
 * Command to list all RBAC roles.
 *
 * <p>Lists all roles in the project, including both project-level and tenant-specific roles.
 */
@Command(name = "list-roles", description = "List all RBAC roles", mixinStandardHelpOptions = true)
public class ListRolesCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(ListRolesCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Inject private ConfigurationService configService;
  @Inject private RoleService roleService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      logger.info("Listing all roles");

      // List the roles
      OperationResult<List<Role>> result = roleService.listRoles(config);

      // Format and print the result
      if (globalOptions.getOutputFormat() == OutputFormat.JSON) {
        String output = outputFormatter.format(result, globalOptions.getOutputFormat());
        System.out.println(output);
      } else {
        printTextResult(result);
      }

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to list roles", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }

  private void printTextResult(OperationResult<List<Role>> result) {
    List<Role> roles = result.getData();
    StringBuilder sb = new StringBuilder();

    sb.append("\u2713 ").append(result.getMessage()).append("\n");
    sb.append("\u2500".repeat(80)).append("\n");

    if (roles.isEmpty()) {
      sb.append("No roles found.\n");
    } else {
      for (Role role : roles) {
        sb.append("Role: ").append(role.getName()).append("\n");
        if (role.getDescription() != null && !role.getDescription().isEmpty()) {
          sb.append("  Description: ").append(role.getDescription()).append("\n");
        }
        if (role.isTenantRole()) {
          sb.append("  Tenant: ").append(role.getTenantId()).append("\n");
        } else {
          sb.append("  Scope: Project-level\n");
        }
        if (role.getPermissionNames() != null && !role.getPermissionNames().isEmpty()) {
          sb.append("  Permissions: ").append(String.join(", ", role.getPermissionNames()));
          sb.append("\n");
        }
        sb.append("\n");
      }
    }

    System.out.println(sb);
  }
}
