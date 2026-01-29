package com.descope.utils.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

/**
 * Main command for descope-utils CLI application.
 *
 * <p>This is the root command that provides subcommands for managing Descope resources.
 */
@Command(
    name = "descope-utils",
    description = "Command-line utilities for managing Descope resources",
    mixinStandardHelpOptions = true,
    subcommands = {
      CreateAppCommand.class,
      CreateTenantCommand.class,
      CreateUserCommand.class,
      CreateFederatedAppCommand.class,
      AddAppToTenantCommand.class,
      CreateRebacSchemaCommand.class,
      LoadRebacSchemaCommand.class,
      DeleteRebacSchemaCommand.class
    })
public class DescopeUtilsCommand implements Runnable {

  @Mixin private GlobalOptions globalOptions;

  @Override
  public void run() {
    // When no subcommand is specified, show help
    System.out.println("Please specify a subcommand. Use --help to see available commands.");
  }

  /**
   * Gets the global options.
   *
   * @return The global options
   */
  public GlobalOptions getGlobalOptions() {
    return globalOptions;
  }
}
