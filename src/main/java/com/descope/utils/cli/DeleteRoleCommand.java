package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.RoleService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to delete an RBAC role.
 *
 * <p>Deletes a role at project level or from a specific tenant.
 */
@Command(name = "delete-role", description = "Delete an RBAC role", mixinStandardHelpOptions = true)
public class DeleteRoleCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(DeleteRoleCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "Role name to delete")
  private String name;

  @Option(
      names = {"-t", "--tenant"},
      description = "Tenant ID for tenant-specific role (omit for project-level)")
  private String tenantId;

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

      String context = tenantId != null ? " from tenant: " + tenantId : " (project-level)";
      logger.info("Deleting role: {}{}", name, context);

      // Delete the role
      OperationResult<Void> result = roleService.deleteRole(config, name, tenantId);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to delete role", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
