package com.descope.utils.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  @DisplayName("constructor - valid parameters - should create User instance")
  void constructor_validParameters_shouldCreateInstance() {
    // Arrange
    String id = "user-123";
    String loginId = "user@example.com";
    String email = "user@example.com";
    String tenantId = "tenant-123";
    Instant createdAt = Instant.now();

    // Act
    User user = new User(id, loginId, email, tenantId, createdAt);

    // Assert
    assertThat(user.getId()).isEqualTo(id);
    assertThat(user.getLoginId()).isEqualTo(loginId);
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getTenantId()).isEqualTo(tenantId);
    assertThat(user.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("constructor - null id - should throw NullPointerException")
  void constructor_nullId_shouldThrowException() {
    // Arrange
    String loginId = "user@example.com";
    String email = "user@example.com";
    String tenantId = "tenant-123";
    Instant createdAt = Instant.now();

    // Act & Assert
    assertThatThrownBy(() -> new User(null, loginId, email, tenantId, createdAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("User ID cannot be null");
  }

  @Test
  @DisplayName("constructor - null loginId - should throw NullPointerException")
  void constructor_nullLoginId_shouldThrowException() {
    // Arrange
    String id = "user-123";
    String email = "user@example.com";
    String tenantId = "tenant-123";
    Instant createdAt = Instant.now();

    // Act & Assert
    assertThatThrownBy(() -> new User(id, null, email, tenantId, createdAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Login ID cannot be null");
  }

  @Test
  @DisplayName("constructor - null tenantId - should throw NullPointerException")
  void constructor_nullTenantId_shouldThrowException() {
    // Arrange
    String id = "user-123";
    String loginId = "user@example.com";
    String email = "user@example.com";
    Instant createdAt = Instant.now();

    // Act & Assert
    assertThatThrownBy(() -> new User(id, loginId, email, null, createdAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Tenant ID cannot be null");
  }

  @Test
  @DisplayName("constructor - null email - should create instance with null email")
  void constructor_nullEmail_shouldCreateInstance() {
    // Arrange
    String id = "user-123";
    String loginId = "user@example.com";
    String tenantId = "tenant-123";
    Instant createdAt = Instant.now();

    // Act
    User user = new User(id, loginId, null, tenantId, createdAt);

    // Assert
    assertThat(user.getEmail()).isNull();
  }

  @Test
  @DisplayName("equals - same values - should return true")
  void equals_sameValues_shouldReturnTrue() {
    // Arrange
    Instant now = Instant.now();
    User user1 = new User("user-123", "user@example.com", "user@example.com", "tenant-123", now);
    User user2 = new User("user-123", "user@example.com", "user@example.com", "tenant-123", now);

    // Act & Assert
    assertThat(user1).isEqualTo(user2);
    assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
  }

  @Test
  @DisplayName("equals - different values - should return false")
  void equals_differentValues_shouldReturnFalse() {
    // Arrange
    Instant now = Instant.now();
    User user1 = new User("user-123", "user@example.com", "user@example.com", "tenant-123", now);
    User user2 = new User("user-456", "other@example.com", "other@example.com", "tenant-456", now);

    // Act & Assert
    assertThat(user1).isNotEqualTo(user2);
  }

  @Test
  @DisplayName("toString - valid user - should return formatted string")
  void toString_validUser_shouldReturnFormattedString() {
    // Arrange
    Instant now = Instant.now();
    User user = new User("user-123", "user@example.com", "user@example.com", "tenant-123", now);

    // Act
    String result = user.toString();

    // Assert
    assertThat(result)
        .contains("user-123")
        .contains("user@example.com")
        .contains("tenant-123")
        .contains(now.toString());
  }
}
