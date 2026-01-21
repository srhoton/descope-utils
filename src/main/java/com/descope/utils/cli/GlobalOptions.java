package com.descope.utils.cli;

import com.descope.utils.model.OutputFormat;

import picocli.CommandLine.Option;

/**
 * Global options available to all commands.
 *
 * <p>These options can be specified for any command and control cross-cutting concerns like
 * authentication and output formatting.
 */
public class GlobalOptions {

  @Option(
      names = {"-p", "--project-id"},
      description = "Descope project ID (overrides environment/file configuration)")
  private String projectId;

  @Option(
      names = {"-k", "--management-key"},
      description = "Descope management key (overrides environment/file configuration)")
  private String managementKey;

  @Option(
      names = {"-o", "--output"},
      description = "Output format: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE})",
      defaultValue = "TEXT")
  private OutputFormat outputFormat;

  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Display this help message")
  private boolean helpRequested;

  /**
   * Gets the project ID from command line.
   *
   * @return The project ID, or null if not specified
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Gets the management key from command line.
   *
   * @return The management key, or null if not specified
   */
  public String getManagementKey() {
    return managementKey;
  }

  /**
   * Gets the output format.
   *
   * @return The output format (never null, defaults to TEXT)
   */
  public OutputFormat getOutputFormat() {
    return outputFormat != null ? outputFormat : OutputFormat.TEXT;
  }

  /**
   * Checks if help was requested.
   *
   * @return true if help was requested, false otherwise
   */
  public boolean isHelpRequested() {
    return helpRequested;
  }
}
