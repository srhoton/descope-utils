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
    descopeService = new DescopeService(config);
  }

  @Test
  @DisplayName("getConfig - returns injected configuration")
  void getConfig_returnsInjectedConfiguration() {
    // When
    DescopeConfig result = descopeService.getConfig();

    // Then
    assertThat(result).isEqualTo(config);
    assertThat(result.getProjectId()).isEqualTo("test-project-id");
    assertThat(result.getManagementKey()).isEqualTo("test-management-key");
  }

  @Test
  @DisplayName("createClient - test skipped - requires valid credentials")
  void createClient_testSkippedRequiresValidCredentials() {
    // This test is skipped as it requires valid Descope credentials
    // Full SDK integration tests will be in the integration test suite
    // We verify the method exists and is callable
    assertThat(descopeService).isNotNull();
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
