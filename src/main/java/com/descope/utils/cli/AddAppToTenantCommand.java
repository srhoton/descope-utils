package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.TenantService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to associate an application with a tenant.
 *
 * <p>This command associates an application (either inbound or federated/SSO) with a tenant, making
 * the application available for users in that tenant to access.
 */
@Command(
    name = "add-app-to-tenant",
    description = "Associate an application with a tenant",
    mixinStandardHelpOptions = true)
public class AddAppToTenantCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(AddAppToTenantCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"-t", "--tenant-id"},
      description = "Tenant ID",
      required = true)
  private String tenantId;

  @Option(
      names = {"-a", "--app-id"},
      description = "Application ID (inbound or federated/SSO app)",
      required = true)
  private String appId;

  @Inject private ConfigurationService configService;
  @Inject private TenantService tenantService;
  @Inject private OutputFormatter outputFormatter;

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      logger.info("Associating app '{}' with tenant '{}'", appId, tenantId);

      // Add app to tenant
      OperationResult<String> result = tenantService.addAppToTenant(config, tenantId, appId);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to add app to tenant", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
