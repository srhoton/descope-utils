package com.descope.utils.output;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.model.Application;
import com.descope.utils.model.OperationResult;

public class JsonFormatterTest {

  private JsonFormatter formatter;

  @BeforeEach
  void setUp() {
    formatter = new JsonFormatter();
  }

  @Test
  @DisplayName("format - successful result - should return JSON with success true")
  void format_successfulResult_shouldReturnJsonWithSuccessTrue() {
    // Arrange
    Application app = new Application("app-123", "Test App", "Description", Instant.now());
    OperationResult<Application> result = OperationResult.success(app, "Created successfully");

    // Act
    String json = formatter.format(result);

    // Assert
    assertThat(json)
        .contains("\"success\" : true")
        .contains("\"message\" : \"Created successfully\"")
        .contains("\"data\"")
        .contains("app-123")
        .doesNotContain("\"error\"");
  }

  @Test
  @DisplayName("format - failed result - should return JSON with success false")
  void format_failedResult_shouldReturnJsonWithSuccessFalse() {
    // Arrange
    OperationResult<Application> result = OperationResult.failure("Operation failed");

    // Act
    String json = formatter.format(result);

    // Assert
    assertThat(json)
        .contains("\"success\" : false")
        .contains("\"error\" : \"Operation failed\"")
        .doesNotContain("\"data\"");
  }

  @Test
  @DisplayName("format - successful result with null data - should handle gracefully")
  void format_successfulResultWithNullData_shouldHandleGracefully() {
    // Arrange
    OperationResult<Application> result = OperationResult.success(null, "No data available");

    // Act
    String json = formatter.format(result);

    // Assert
    assertThat(json).contains("\"success\" : true").contains("\"message\" : \"No data available\"");
  }

  @Test
  @DisplayName("format - result with special characters - should escape properly")
  void format_resultWithSpecialCharacters_shouldEscapeProperly() {
    // Arrange
    Application app =
        new Application("app-123", "Test \"App\"", "Description with\nnewline", Instant.now());
    OperationResult<Application> result = OperationResult.success(app, "Created");

    // Act
    String json = formatter.format(result);

    // Assert
    assertThat(json).contains("Test \\\"App\\\"").contains("Description with\\nnewline");
  }
}
