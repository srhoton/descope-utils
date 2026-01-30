package com.descope.utils.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.Role;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.RoleService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to update an existing RBAC role.
 *
 * <p>Updates a role's name, description, or permissions.
 */
@Command(
    name = "update-role",
    description = "Update an existing RBAC role",
    mixinStandardHelpOptions = true)
public class UpdateRoleCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(UpdateRoleCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "Current role name")
  private String name;

  @Option(
      names = {"-n", "--new-name"},
      description = "New role name (if renaming)")
  private String newName;

  @Option(
      names = {"-d", "--description"},
      description = "New role description")
  private String description;

  @Option(
      names = {"-t", "--tenant"},
      description = "Tenant ID for tenant-specific role (omit for project-level)")
  private String tenantId;

  @Option(
      names = {"--permissions"},
      description = "Comma-separated list of permission names (replaces existing)",
      split = ",")
  private List<String> permissionNames;

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

      // Use current name if new name not provided
      String effectiveNewName = newName != null ? newName : name;

      String context = tenantId != null ? " in tenant: " + tenantId : " (project-level)";
      logger.info("Updating role: {}{}", name, context);

      // Update the role
      OperationResult<Role> result =
          roleService.updateRole(
              config, name, tenantId, effectiveNewName, description, permissionNames);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to update role", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
