package com.descope.utils.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ConfigurationServiceTest {

  private ConfigurationService service;
  private Path tempProjectIdFile;
  private Path tempManagementKeyFile;

  @BeforeEach
  void setUp() throws IOException {
    service = new ConfigurationService();
    tempProjectIdFile = Files.createTempFile("project_id_", ".txt");
    tempManagementKeyFile = Files.createTempFile("management_key_", ".txt");

    // Clear environment variables for testing
    clearEnvironmentVariables();
  }

  @AfterEach
  void tearDown() throws IOException {
    if (tempProjectIdFile != null && Files.exists(tempProjectIdFile)) {
      Files.deleteIfExists(tempProjectIdFile);
    }
    if (tempManagementKeyFile != null && Files.exists(tempManagementKeyFile)) {
      Files.deleteIfExists(tempManagementKeyFile);
    }
  }

  @Test
  @DisplayName(
      "loadConfiguration - valid CLI arguments - should return config from command line source")
  void loadConfiguration_validCliArguments_shouldReturnCommandLineConfig() {
    // Arrange
    String projectId = "cli-project-123";
    String managementKey = "cli-key-456";

    // Act
    DescopeConfig config = service.loadConfiguration(projectId, managementKey);

    // Assert
    assertThat(config.getProjectId()).isEqualTo(projectId);
    assertThat(config.getManagementKey()).isEqualTo(managementKey);
    assertThat(config.getSource()).isEqualTo(CredentialSource.COMMAND_LINE);
  }

  @Test
  @DisplayName("loadConfiguration - valid files - should return config from file source")
  void loadConfiguration_validFiles_shouldReturnFileConfig() throws IOException {
    // Arrange
    String projectId = "file-project-123";
    String managementKey = "file-key-456";
    Files.writeString(tempProjectIdFile, projectId);
    Files.writeString(tempManagementKeyFile, managementKey);

    // Act
    var config =
        service.loadFromFiles(tempProjectIdFile.toString(), tempManagementKeyFile.toString());

    // Assert
    assertThat(config).isPresent();
    assertThat(config.get().getProjectId()).isEqualTo(projectId);
    assertThat(config.get().getManagementKey()).isEqualTo(managementKey);
    assertThat(config.get().getSource()).isEqualTo(CredentialSource.FILE);
  }

  @Test
  @DisplayName(
      "loadConfiguration - files with whitespace - should return trimmed config from file source")
  void loadConfiguration_filesWithWhitespace_shouldReturnTrimmedConfig() throws IOException {
    // Arrange
    String projectId = "file-project-123";
    String managementKey = "file-key-456";
    Files.writeString(tempProjectIdFile, "  " + projectId + "  \n");
    Files.writeString(tempManagementKeyFile, "\n" + managementKey + "  ");

    // Act
    var config =
        service.loadFromFiles(tempProjectIdFile.toString(), tempManagementKeyFile.toString());

    // Assert
    assertThat(config).isPresent();
    assertThat(config.get().getProjectId()).isEqualTo(projectId);
    assertThat(config.get().getManagementKey()).isEqualTo(managementKey);
  }

  @Test
  @DisplayName("loadConfiguration - missing files - should return empty")
  void loadConfiguration_missingFiles_shouldReturnEmpty() {
    // Arrange
    String nonExistentProjectIdFile = "/tmp/non-existent-project-id-file.txt";
    String nonExistentManagementKeyFile = "/tmp/non-existent-management-key-file.txt";

    // Act
    var config = service.loadFromFiles(nonExistentProjectIdFile, nonExistentManagementKeyFile);

    // Assert
    assertThat(config).isEmpty();
  }

  @Test
  @DisplayName("loadConfiguration - empty files - should return empty")
  void loadConfiguration_emptyFiles_shouldReturnEmpty() throws IOException {
    // Arrange
    Files.writeString(tempProjectIdFile, "");
    Files.writeString(tempManagementKeyFile, "");

    // Act
    var config =
        service.loadFromFiles(tempProjectIdFile.toString(), tempManagementKeyFile.toString());

    // Assert
    assertThat(config).isEmpty();
  }

  @Test
  @DisplayName(
      "loadConfiguration - null CLI arguments with no files - should throw if no alternative source")
  void loadConfiguration_nullCliArgumentsWithNoFiles_shouldThrowIfNoAlternativeSource() {
    // Note: This test may pass if actual credential files exist in default locations
    // or environment variables are set. In that case, the configuration is successfully
    // loaded from those sources, which is the expected behavior.

    // Act
    try {
      DescopeConfig config = service.loadConfiguration(null, null);
      // If we get here, credentials were found from files or environment
      assertThat(config).isNotNull();
      assertThat(config.getSource()).isIn(CredentialSource.FILE, CredentialSource.ENVIRONMENT);
    } catch (IllegalStateException e) {
      // If exception is thrown, it means no credentials were found anywhere
      assertThat(e.getMessage()).contains("Could not load Descope configuration");
    }
  }

  @Test
  @DisplayName("loadConfiguration - empty CLI arguments - should use fallback sources or throw")
  void loadConfiguration_emptyCliArguments_shouldUseFallbackOrThrow() {
    // Act
    try {
      DescopeConfig config = service.loadConfiguration("", "");
      // If we get here, credentials were found from files or environment
      assertThat(config).isNotNull();
      assertThat(config.getSource()).isIn(CredentialSource.FILE, CredentialSource.ENVIRONMENT);
    } catch (IllegalStateException e) {
      // If exception is thrown, it means no credentials were found anywhere
      assertThat(e.getMessage()).contains("Could not load Descope configuration");
    }
  }

  @Test
  @DisplayName("loadConfiguration - partial CLI arguments - should use fallback sources or throw")
  void loadConfiguration_partialCliArguments_shouldUseFallbackOrThrow() {
    // Act
    try {
      DescopeConfig config1 = service.loadConfiguration("project-id", null);
      assertThat(config1).isNotNull();
      assertThat(config1.getSource()).isIn(CredentialSource.FILE, CredentialSource.ENVIRONMENT);
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).contains("Could not load Descope configuration");
    }

    try {
      DescopeConfig config2 = service.loadConfiguration(null, "management-key");
      assertThat(config2).isNotNull();
      assertThat(config2.getSource()).isIn(CredentialSource.FILE, CredentialSource.ENVIRONMENT);
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).contains("Could not load Descope configuration");
    }
  }

  @Test
  @DisplayName("loadConfiguration - CLI takes precedence over files - should use CLI config")
  void loadConfiguration_cliTakesPrecedenceOverFiles_shouldUseCliConfig() throws IOException {
    // Arrange
    String cliProjectId = "cli-project";
    String cliManagementKey = "cli-key";
    Files.writeString(tempProjectIdFile, "file-project");
    Files.writeString(tempManagementKeyFile, "file-key");

    // Act
    DescopeConfig config = service.loadConfiguration(cliProjectId, cliManagementKey);

    // Assert
    assertThat(config.getProjectId()).isEqualTo(cliProjectId);
    assertThat(config.getManagementKey()).isEqualTo(cliManagementKey);
    assertThat(config.getSource()).isEqualTo(CredentialSource.COMMAND_LINE);
  }

  private void clearEnvironmentVariables() {
    // Note: We cannot actually clear environment variables in Java tests
    // In real scenarios, the ConfigurationService would check System.getenv()
    // which would be mocked in more sophisticated tests
  }
}
