package com.descope.utils.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OperationResultTest {

  @Test
  @DisplayName("success - with data and message - should create successful result")
  void success_withDataAndMessage_shouldCreateSuccessfulResult() {
    // Arrange
    String data = "test data";
    String message = "Operation succeeded";

    // Act
    OperationResult<String> result = OperationResult.success(data, message);

    // Assert
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getData()).isPresent().contains(data);
    assertThat(result.getMessage()).isEqualTo(message);
    assertThat(result.getErrorMessage()).isNull();
  }

  @Test
  @DisplayName("success - with data only - should create successful result with default message")
  void success_withDataOnly_shouldCreateSuccessfulResultWithDefaultMessage() {
    // Arrange
    String data = "test data";

    // Act
    OperationResult<String> result = OperationResult.success(data);

    // Assert
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getData()).isPresent().contains(data);
    assertThat(result.getMessage()).isEqualTo("Operation completed successfully");
    assertThat(result.getErrorMessage()).isNull();
  }

  @Test
  @DisplayName("failure - with error message - should create failed result")
  void failure_withErrorMessage_shouldCreateFailedResult() {
    // Arrange
    String errorMessage = "Operation failed";

    // Act
    OperationResult<String> result = OperationResult.failure(errorMessage);

    // Assert
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getData()).isEmpty();
    assertThat(result.getMessage()).isNull();
    assertThat(result.getErrorMessage()).isEqualTo(errorMessage);
  }

  @Test
  @DisplayName("failure - null error message - should throw NullPointerException")
  void failure_nullErrorMessage_shouldThrowException() {
    // Act & Assert
    assertThatThrownBy(() -> OperationResult.failure(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Error message cannot be null");
  }

  @Test
  @DisplayName("equals - same successful results - should return true")
  void equals_sameSuccessfulResults_shouldReturnTrue() {
    // Arrange
    OperationResult<String> result1 = OperationResult.success("data", "message");
    OperationResult<String> result2 = OperationResult.success("data", "message");

    // Act & Assert
    assertThat(result1).isEqualTo(result2);
    assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
  }

  @Test
  @DisplayName("equals - same failed results - should return true")
  void equals_sameFailedResults_shouldReturnTrue() {
    // Arrange
    OperationResult<String> result1 = OperationResult.failure("error");
    OperationResult<String> result2 = OperationResult.failure("error");

    // Act & Assert
    assertThat(result1).isEqualTo(result2);
    assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
  }

  @Test
  @DisplayName("equals - different results - should return false")
  void equals_differentResults_shouldReturnFalse() {
    // Arrange
    OperationResult<String> result1 = OperationResult.success("data");
    OperationResult<String> result2 = OperationResult.failure("error");

    // Act & Assert
    assertThat(result1).isNotEqualTo(result2);
  }

  @Test
  @DisplayName("toString - successful result - should return formatted string")
  void toString_successfulResult_shouldReturnFormattedString() {
    // Arrange
    OperationResult<String> result = OperationResult.success("data", "message");

    // Act
    String stringResult = result.toString();

    // Assert
    assertThat(stringResult).contains("success=true").contains("message").contains("data");
  }

  @Test
  @DisplayName("toString - failed result - should return formatted string")
  void toString_failedResult_shouldReturnFormattedString() {
    // Arrange
    OperationResult<String> result = OperationResult.failure("error message");

    // Act
    String stringResult = result.toString();

    // Assert
    assertThat(stringResult).contains("success=false").contains("error message");
  }

  @Test
  @DisplayName("success - null data - should create successful result with null data")
  void success_nullData_shouldCreateSuccessfulResult() {
    // Act
    OperationResult<String> result = OperationResult.success(null, "message");

    // Assert
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getData()).isEmpty();
    assertThat(result.getMessage()).isEqualTo("message");
  }
}
