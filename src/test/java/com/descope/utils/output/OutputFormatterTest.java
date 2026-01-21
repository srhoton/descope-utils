package com.descope.utils.output;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.model.Application;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.OutputFormat;

public class OutputFormatterTest {

  private final OutputFormatter formatter =
      new OutputFormatter(new JsonFormatter(), new TextFormatter());

  @Test
  @DisplayName("format - successful result with JSON format - should return JSON string")
  void format_successfulResultWithJsonFormat_shouldReturnJsonString() {
    // Arrange
    Application app = new Application("app-123", "Test App", "Description", Instant.now());
    OperationResult<Application> result = OperationResult.success(app, "Application created");

    // Act
    String output = formatter.format(result, OutputFormat.JSON);

    // Assert
    assertThat(output)
        .contains("\"success\"")
        .contains("true")
        .contains("app-123")
        .contains("Test App");
  }

  @Test
  @DisplayName("format - successful result with TEXT format - should return text string")
  void format_successfulResultWithTextFormat_shouldReturnTextString() {
    // Arrange
    Application app = new Application("app-123", "Test App", "Description", Instant.now());
    OperationResult<Application> result = OperationResult.success(app, "Application created");

    // Act
    String output = formatter.format(result, OutputFormat.TEXT);

    // Assert
    assertThat(output).contains("✓").contains("Application created").contains("app-123");
  }

  @Test
  @DisplayName("format - failed result with JSON format - should return error JSON")
  void format_failedResultWithJsonFormat_shouldReturnErrorJson() {
    // Arrange
    OperationResult<Application> result = OperationResult.failure("Something went wrong");

    // Act
    String output = formatter.format(result, OutputFormat.JSON);

    // Assert
    assertThat(output).contains("\"success\"").contains("false").contains("Something went wrong");
  }

  @Test
  @DisplayName("format - failed result with TEXT format - should return error text")
  void format_failedResultWithTextFormat_shouldReturnErrorText() {
    // Arrange
    OperationResult<Application> result = OperationResult.failure("Something went wrong");

    // Act
    String output = formatter.format(result, OutputFormat.TEXT);

    // Assert
    assertThat(output).contains("✗").contains("Error").contains("Something went wrong");
  }
}
