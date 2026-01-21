package com.descope.utils.output;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.model.Application;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.Tenant;
import com.descope.utils.model.User;

public class TextFormatterTest {

  private TextFormatter formatter;

  @BeforeEach
  void setUp() {
    formatter = new TextFormatter();
  }

  @Test
  @DisplayName("format - successful result with Application - should format application details")
  void format_successfulResultWithApplication_shouldFormatApplicationDetails() {
    // Arrange
    Application app = new Application("app-123", "Test App", "Test Description", Instant.now());
    OperationResult<Application> result = OperationResult.success(app, "Application created");

    // Act
    String text = formatter.format(result);

    // Assert
    assertThat(text)
        .contains("✓")
        .contains("Application created")
        .contains("Application Details:")
        .contains("ID:")
        .contains("app-123")
        .contains("Name:")
        .contains("Test App")
        .contains("Description:")
        .contains("Test Description");
  }

  @Test
  @DisplayName("format - successful result with Tenant - should format tenant details")
  void format_successfulResultWithTenant_shouldFormatTenantDetails() {
    // Arrange
    Tenant tenant = new Tenant("tenant-123", "Test Tenant", "app-456", Instant.now());
    OperationResult<Tenant> result = OperationResult.success(tenant, "Tenant created");

    // Act
    String text = formatter.format(result);

    // Assert
    assertThat(text)
        .contains("✓")
        .contains("Tenant created")
        .contains("Tenant Details:")
        .contains("ID:")
        .contains("tenant-123")
        .contains("Name:")
        .contains("Test Tenant")
        .contains("App ID:")
        .contains("app-456");
  }

  @Test
  @DisplayName("format - successful result with User - should format user details")
  void format_successfulResultWithUser_shouldFormatUserDetails() {
    // Arrange
    User user =
        new User("user-123", "user@example.com", "user@example.com", "tenant-456", Instant.now());
    OperationResult<User> result = OperationResult.success(user, "User created");

    // Act
    String text = formatter.format(result);

    // Assert
    assertThat(text)
        .contains("✓")
        .contains("User created")
        .contains("User Details:")
        .contains("ID:")
        .contains("user-123")
        .contains("Login ID:")
        .contains("user@example.com")
        .contains("Email:")
        .contains("Tenant ID:")
        .contains("tenant-456");
  }

  @Test
  @DisplayName("format - failed result - should format error message")
  void format_failedResult_shouldFormatErrorMessage() {
    // Arrange
    OperationResult<Application> result = OperationResult.failure("Something went wrong");

    // Act
    String text = formatter.format(result);

    // Assert
    assertThat(text).contains("✗").contains("Error:").contains("Something went wrong");
  }

  @Test
  @DisplayName("format - application without optional fields - should handle gracefully")
  void format_applicationWithoutOptionalFields_shouldHandleGracefully() {
    // Arrange
    Application app = new Application("app-123", "Test App", null, null);
    OperationResult<Application> result = OperationResult.success(app);

    // Act
    String text = formatter.format(result);

    // Assert
    assertThat(text)
        .contains("Application Details:")
        .contains("app-123")
        .contains("Test App")
        .doesNotContain("Description:")
        .doesNotContain("Created:");
  }

  @Test
  @DisplayName("format - user without optional email - should handle gracefully")
  void format_userWithoutOptionalEmail_shouldHandleGracefully() {
    // Arrange
    User user = new User("user-123", "user-login", null, "tenant-456", null);
    OperationResult<User> result = OperationResult.success(user);

    // Act
    String text = formatter.format(result);

    // Assert
    assertThat(text)
        .contains("User Details:")
        .contains("user-123")
        .contains("user-login")
        .doesNotContain("Email:");
  }
}
