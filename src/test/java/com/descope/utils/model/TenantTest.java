package com.descope.utils.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TenantTest {

  @Test
  @DisplayName("constructor - valid parameters - should create Tenant instance")
  void constructor_validParameters_shouldCreateInstance() {
    // Arrange
    String id = "tenant-123";
    String name = "Test Tenant";
    String appId = "app-123";
    Instant createdAt = Instant.now();

    // Act
    Tenant tenant = new Tenant(id, name, appId, createdAt);

    // Assert
    assertThat(tenant.getId()).isEqualTo(id);
    assertThat(tenant.getName()).isEqualTo(name);
    assertThat(tenant.getAppId()).isEqualTo(appId);
    assertThat(tenant.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("constructor - null id - should throw NullPointerException")
  void constructor_nullId_shouldThrowException() {
    // Arrange
    String name = "Test Tenant";
    String appId = "app-123";
    Instant createdAt = Instant.now();

    // Act & Assert
    assertThatThrownBy(() -> new Tenant(null, name, appId, createdAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Tenant ID cannot be null");
  }

  @Test
  @DisplayName("constructor - null name - should throw NullPointerException")
  void constructor_nullName_shouldThrowException() {
    // Arrange
    String id = "tenant-123";
    String appId = "app-123";
    Instant createdAt = Instant.now();

    // Act & Assert
    assertThatThrownBy(() -> new Tenant(id, null, appId, createdAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Tenant name cannot be null");
  }

  @Test
  @DisplayName("constructor - null appId - should throw NullPointerException")
  void constructor_nullAppId_shouldThrowException() {
    // Arrange
    String id = "tenant-123";
    String name = "Test Tenant";
    Instant createdAt = Instant.now();

    // Act & Assert
    assertThatThrownBy(() -> new Tenant(id, name, null, createdAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Application ID cannot be null");
  }

  @Test
  @DisplayName("equals - same values - should return true")
  void equals_sameValues_shouldReturnTrue() {
    // Arrange
    Instant now = Instant.now();
    Tenant tenant1 = new Tenant("tenant-123", "Test Tenant", "app-123", now);
    Tenant tenant2 = new Tenant("tenant-123", "Test Tenant", "app-123", now);

    // Act & Assert
    assertThat(tenant1).isEqualTo(tenant2);
    assertThat(tenant1.hashCode()).isEqualTo(tenant2.hashCode());
  }

  @Test
  @DisplayName("equals - different values - should return false")
  void equals_differentValues_shouldReturnFalse() {
    // Arrange
    Instant now = Instant.now();
    Tenant tenant1 = new Tenant("tenant-123", "Test Tenant", "app-123", now);
    Tenant tenant2 = new Tenant("tenant-456", "Different Tenant", "app-456", now);

    // Act & Assert
    assertThat(tenant1).isNotEqualTo(tenant2);
  }

  @Test
  @DisplayName("toString - valid tenant - should return formatted string")
  void toString_validTenant_shouldReturnFormattedString() {
    // Arrange
    Instant now = Instant.now();
    Tenant tenant = new Tenant("tenant-123", "Test Tenant", "app-123", now);

    // Act
    String result = tenant.toString();

    // Assert
    assertThat(result)
        .contains("tenant-123")
        .contains("Test Tenant")
        .contains("app-123")
        .contains(now.toString());
  }
}
