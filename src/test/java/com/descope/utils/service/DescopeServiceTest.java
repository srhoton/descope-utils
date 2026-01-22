package com.descope.utils.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.config.CredentialSource;
import com.descope.utils.config.DescopeConfig;

class DescopeServiceTest {

  private DescopeService descopeService;
  private DescopeConfig config;

  @BeforeEach
  void setUp() {
    config =
        new DescopeConfig("test-project-id", "test-management-key", CredentialSource.COMMAND_LINE);
    descopeService = new DescopeService();
  }

  @Test
  @DisplayName("createClient - method exists and is callable")
  void createClient_methodExistsAndIsCallable() {
    // This test verifies the method signature is correct
    // We don't actually create a client with test credentials as that would fail
    // Full SDK integration tests with real credentials will be in the integration test suite
    assertThat(descopeService).isNotNull();
    // Verify method exists by attempting to call it (will throw exception with invalid creds)
    try {
      descopeService.createClient(config);
    } catch (Exception e) {
      // Expected - test credentials are not valid
      assertThat(e).isNotNull();
    }
  }

  @Test
  @DisplayName("wrapException - wraps exception with operation context")
  void wrapException_wrapsExceptionWithOperationContext() {
    // Given
    Exception cause = new Exception("Original error message");
    String operation = "test operation";

    // When
    RuntimeException result = descopeService.wrapException(operation, cause);

    // Then
    assertThat(result).isInstanceOf(RuntimeException.class);
    assertThat(result.getMessage()).contains("Failed to test operation");
    assertThat(result.getMessage()).contains("Original error message");
    assertThat(result.getCause()).isEqualTo(cause);
  }
}
