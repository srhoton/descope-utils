package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.Tenant;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.TenantService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to create a Descope tenant.
 *
 * <p>Creates a new tenant in Descope with the specified name and optionally associates it with an
 * application.
 */
@Command(
    name = "create-tenant",
    description = "Create a new Descope tenant",
    mixinStandardHelpOptions = true)
public class CreateTenantCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(CreateTenantCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "Tenant name")
  private String name;

  @Option(
      names = {"-a", "--app-id"},
      description = "Application ID to associate the tenant with")
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

      logger.info("Creating tenant: {}", name);

      // Create the tenant
      OperationResult<Tenant> result = tenantService.createTenant(config, name, appId);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to create tenant", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
