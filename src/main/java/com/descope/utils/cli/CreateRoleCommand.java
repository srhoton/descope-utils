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
 * Command to create a new RBAC role.
 *
 * <p>Creates a role at project level or within a specific tenant.
 */
@Command(
    name = "create-role",
    description = "Create a new RBAC role",
    mixinStandardHelpOptions = true)
public class CreateRoleCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CreateRoleCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "Role name")
  private String name;

  @Option(
      names = {"-d", "--description"},
      description = "Role description")
  private String description;

  @Option(
      names = {"-t", "--tenant"},
      description = "Tenant ID for tenant-specific role (omit for project-level)")
  private String tenantId;

  @Option(
      names = {"--permissions"},
      description = "Comma-separated list of permission names to associate with this role",
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

      String context = tenantId != null ? " in tenant: " + tenantId : " (project-level)";
      logger.info("Creating role: {}{}", name, context);

      // Create the role
      OperationResult<Role> result =
          roleService.createRole(config, name, tenantId, description, permissionNames);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to create role", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
