package com.descope.utils.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service for loading Descope configuration from multiple sources.
 *
 * <p>Configuration is loaded with the following precedence (highest to lowest):
 *
 * <ol>
 *   <li>Command-line arguments (if provided)
 *   <li>Environment variables (DESCOPE_PROJECT_ID, DESCOPE_MANAGEMENT_KEY)
 *   <li>Files (~/git/tmp/descope/project_id, ~/git/tmp/descope/management_key)
 * </ol>
 */
@ApplicationScoped
public class ConfigurationService {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

  private static final String ENV_PROJECT_ID = "DESCOPE_PROJECT_ID";
  private static final String ENV_MANAGEMENT_KEY = "DESCOPE_MANAGEMENT_KEY";
  private static final String DEFAULT_PROJECT_ID_FILE =
      System.getProperty("user.home") + "/git/tmp/descope/project_id";
  private static final String DEFAULT_MANAGEMENT_KEY_FILE =
      System.getProperty("user.home") + "/git/tmp/descope/management_key";

  /**
   * Loads Descope configuration from available sources.
   *
   * @param cliProjectId Optional project ID from command line
   * @param cliManagementKey Optional management key from command line
   * @return The loaded configuration
   * @throws IllegalStateException if configuration cannot be loaded from any source
   */
  public DescopeConfig loadConfiguration(String cliProjectId, String cliManagementKey) {
    // Try command-line arguments first
    if (isValidCredential(cliProjectId) && isValidCredential(cliManagementKey)) {
      logger.info("Using Descope credentials from command-line arguments");
      return new DescopeConfig(cliProjectId, cliManagementKey, CredentialSource.COMMAND_LINE);
    }

    // Try environment variables
    Optional<DescopeConfig> envConfig = loadFromEnvironment();
    if (envConfig.isPresent()) {
      logger.info("Using Descope credentials from environment variables");
      return envConfig.get();
    }

    // Try files
    Optional<DescopeConfig> fileConfig = loadFromFiles();
    if (fileConfig.isPresent()) {
      logger.info("Using Descope credentials from files");
      return fileConfig.get();
    }

    throw new IllegalStateException(
        "Could not load Descope configuration. Please provide credentials via "
            + "command-line arguments, environment variables, or credential files.");
  }

  /**
   * Loads configuration from environment variables.
   *
   * @return Optional containing configuration if both variables are set, empty otherwise
   */
  private Optional<DescopeConfig> loadFromEnvironment() {
    String projectId = System.getenv(ENV_PROJECT_ID);
    String managementKey = System.getenv(ENV_MANAGEMENT_KEY);

    if (isValidCredential(projectId) && isValidCredential(managementKey)) {
      return Optional.of(new DescopeConfig(projectId, managementKey, CredentialSource.ENVIRONMENT));
    }

    return Optional.empty();
  }

  /**
   * Loads configuration from default file locations.
   *
   * @return Optional containing configuration if both files exist and are readable, empty otherwise
   */
  private Optional<DescopeConfig> loadFromFiles() {
    return loadFromFiles(DEFAULT_PROJECT_ID_FILE, DEFAULT_MANAGEMENT_KEY_FILE);
  }

  /**
   * Loads configuration from specified file paths.
   *
   * @param projectIdPath Path to the project ID file
   * @param managementKeyPath Path to the management key file
   * @return Optional containing configuration if both files exist and are readable, empty otherwise
   */
  Optional<DescopeConfig> loadFromFiles(String projectIdPath, String managementKeyPath) {
    try {
      Path projectIdFile = Paths.get(projectIdPath);
      Path managementKeyFile = Paths.get(managementKeyPath);

      if (!Files.exists(projectIdFile) || !Files.exists(managementKeyFile)) {
        logger.debug("Credential files not found at default locations");
        return Optional.empty();
      }

      String projectId = Files.readString(projectIdFile).trim();
      String managementKey = Files.readString(managementKeyFile).trim();

      if (isValidCredential(projectId) && isValidCredential(managementKey)) {
        return Optional.of(new DescopeConfig(projectId, managementKey, CredentialSource.FILE));
      }

      logger.warn("Credential files exist but contain invalid data");
      return Optional.empty();

    } catch (IOException e) {
      logger.error("Error reading credential files", e);
      return Optional.empty();
    }
  }

  /**
   * Validates if a credential string is non-null and non-empty.
   *
   * @param credential The credential to validate
   * @return true if valid, false otherwise
   */
  private boolean isValidCredential(String credential) {
    return credential != null && !credential.trim().isEmpty();
  }
}
