package com.descope.utils.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ApplicationTest {

  @Test
  @DisplayName("constructor - valid parameters - should create Application instance")
  void constructor_validParameters_shouldCreateInstance() {
    // Arrange
    String id = "app-123";
    String name = "Test App";
    String description = "Test Description";
    Instant createdAt = Instant.now();

    // Act
    Application app = new Application(id, name, description, createdAt);

    // Assert
    assertThat(app.getId()).isEqualTo(id);
    assertThat(app.getName()).isEqualTo(name);
    assertThat(app.getDescription()).isEqualTo(description);
    assertThat(app.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("constructor - null id - should throw NullPointerException")
  void constructor_nullId_shouldThrowException() {
    // Arrange
    String name = "Test App";
    String description = "Test Description";
    Instant createdAt = Instant.now();

    // Act & Assert
    assertThatThrownBy(() -> new Application(null, name, description, createdAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Application ID cannot be null");
  }

  @Test
  @DisplayName("constructor - null name - should throw NullPointerException")
  void constructor_nullName_shouldThrowException() {
    // Arrange
    String id = "app-123";
    String description = "Test Description";
    Instant createdAt = Instant.now();

    // Act & Assert
    assertThatThrownBy(() -> new Application(id, null, description, createdAt))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Application name cannot be null");
  }

  @Test
  @DisplayName("constructor - null description - should create instance with null description")
  void constructor_nullDescription_shouldCreateInstance() {
    // Arrange
    String id = "app-123";
    String name = "Test App";
    Instant createdAt = Instant.now();

    // Act
    Application app = new Application(id, name, null, createdAt);

    // Assert
    assertThat(app.getDescription()).isNull();
  }

  @Test
  @DisplayName("equals - same values - should return true")
  void equals_sameValues_shouldReturnTrue() {
    // Arrange
    Instant now = Instant.now();
    Application app1 = new Application("app-123", "Test App", "Description", now);
    Application app2 = new Application("app-123", "Test App", "Description", now);

    // Act & Assert
    assertThat(app1).isEqualTo(app2);
    assertThat(app1.hashCode()).isEqualTo(app2.hashCode());
  }

  @Test
  @DisplayName("equals - different values - should return false")
  void equals_differentValues_shouldReturnFalse() {
    // Arrange
    Instant now = Instant.now();
    Application app1 = new Application("app-123", "Test App", "Description", now);
    Application app2 = new Application("app-456", "Different App", "Different Description", now);

    // Act & Assert
    assertThat(app1).isNotEqualTo(app2);
  }

  @Test
  @DisplayName("toString - valid application - should return formatted string")
  void toString_validApplication_shouldReturnFormattedString() {
    // Arrange
    Instant now = Instant.now();
    Application app = new Application("app-123", "Test App", "Description", now);

    // Act
    String result = app.toString();

    // Assert
    assertThat(result)
        .contains("app-123")
        .contains("Test App")
        .contains("Description")
        .contains(now.toString());
  }
}
