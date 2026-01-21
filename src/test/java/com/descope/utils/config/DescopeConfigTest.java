package com.descope.utils.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DescopeConfigTest {

  @Test
  @DisplayName("constructor - valid parameters - should create DescopeConfig instance")
  void constructor_validParameters_shouldCreateInstance() {
    // Arrange
    String projectId = "test-project-123";
    String managementKey = "test-key-456";
    CredentialSource source = CredentialSource.COMMAND_LINE;

    // Act
    DescopeConfig config = new DescopeConfig(projectId, managementKey, source);

    // Assert
    assertThat(config.getProjectId()).isEqualTo(projectId);
    assertThat(config.getManagementKey()).isEqualTo(managementKey);
    assertThat(config.getSource()).isEqualTo(source);
  }

  @Test
  @DisplayName("constructor - null projectId - should throw NullPointerException")
  void constructor_nullProjectId_shouldThrowException() {
    // Arrange
    String managementKey = "test-key-456";
    CredentialSource source = CredentialSource.COMMAND_LINE;

    // Act & Assert
    assertThatThrownBy(() -> new DescopeConfig(null, managementKey, source))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Project ID cannot be null");
  }

  @Test
  @DisplayName("constructor - null managementKey - should throw NullPointerException")
  void constructor_nullManagementKey_shouldThrowException() {
    // Arrange
    String projectId = "test-project-123";
    CredentialSource source = CredentialSource.COMMAND_LINE;

    // Act & Assert
    assertThatThrownBy(() -> new DescopeConfig(projectId, null, source))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Management key cannot be null");
  }

  @Test
  @DisplayName("constructor - null source - should throw NullPointerException")
  void constructor_nullSource_shouldThrowException() {
    // Arrange
    String projectId = "test-project-123";
    String managementKey = "test-key-456";

    // Act & Assert
    assertThatThrownBy(() -> new DescopeConfig(projectId, managementKey, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Credential source cannot be null");
  }

  @Test
  @DisplayName("constructor - empty projectId - should throw IllegalArgumentException")
  void constructor_emptyProjectId_shouldThrowException() {
    // Arrange
    String projectId = "   ";
    String managementKey = "test-key-456";
    CredentialSource source = CredentialSource.COMMAND_LINE;

    // Act & Assert
    assertThatThrownBy(() -> new DescopeConfig(projectId, managementKey, source))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Project ID cannot be empty");
  }

  @Test
  @DisplayName("constructor - empty managementKey - should throw IllegalArgumentException")
  void constructor_emptyManagementKey_shouldThrowException() {
    // Arrange
    String projectId = "test-project-123";
    String managementKey = "   ";
    CredentialSource source = CredentialSource.COMMAND_LINE;

    // Act & Assert
    assertThatThrownBy(() -> new DescopeConfig(projectId, managementKey, source))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Management key cannot be empty");
  }

  @Test
  @DisplayName("equals - same values - should return true")
  void equals_sameValues_shouldReturnTrue() {
    // Arrange
    DescopeConfig config1 =
        new DescopeConfig("project-123", "key-456", CredentialSource.COMMAND_LINE);
    DescopeConfig config2 =
        new DescopeConfig("project-123", "key-456", CredentialSource.COMMAND_LINE);

    // Act & Assert
    assertThat(config1).isEqualTo(config2);
    assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
  }

  @Test
  @DisplayName("equals - different values - should return false")
  void equals_differentValues_shouldReturnFalse() {
    // Arrange
    DescopeConfig config1 =
        new DescopeConfig("project-123", "key-456", CredentialSource.COMMAND_LINE);
    DescopeConfig config2 =
        new DescopeConfig("project-789", "key-012", CredentialSource.ENVIRONMENT);

    // Act & Assert
    assertThat(config1).isNotEqualTo(config2);
  }

  @Test
  @DisplayName("toString - valid config - should mask management key")
  void toString_validConfig_shouldMaskManagementKey() {
    // Arrange
    DescopeConfig config =
        new DescopeConfig("project-123", "secret-key", CredentialSource.COMMAND_LINE);

    // Act
    String result = config.toString();

    // Assert
    assertThat(result).contains("project-123").contains("***").doesNotContain("secret-key");
  }
}
