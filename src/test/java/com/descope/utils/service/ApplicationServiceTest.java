package com.descope.utils.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.config.CredentialSource;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.Application;
import com.descope.utils.model.OperationResult;

/**
 * Unit tests for ApplicationService SDK integration.
 *
 * <p>NOTE: These tests require valid Descope credentials and network access. They are disabled by
 * default. Integration tests in the integration test suite will test the full flow.
 */
@org.junit.jupiter.api.Disabled("Requires real Descope credentials and network access")
class ApplicationServiceTest {

  private DescopeService descopeService;
  private ApplicationService applicationService;
  private DescopeConfig config;

  @BeforeEach
  void setUp() {
    config =
        new DescopeConfig("test-project-id", "test-management-key", CredentialSource.COMMAND_LINE);
    descopeService = new DescopeService();
    applicationService = new ApplicationService(descopeService);
  }

  @Test
  @DisplayName("createApplication - valid name and description - returns created application")
  void createApplication_validNameAndDescription_returnsCreatedApplication() {
    // Given
    String name = "Test App";
    String description = "Test Description";

    // When
    OperationResult<Application> result =
        applicationService.createApplication(config, name, description);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.isCreated()).isTrue();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getName()).isEqualTo(name);
    assertThat(result.getData().getDescription()).isEqualTo(description);
    assertThat(result.getData().getId()).isNotNull();
    assertThat(result.getMessage()).contains("created successfully");
  }

  @Test
  @DisplayName("createApplication - valid name without description - returns created application")
  void createApplication_validNameWithoutDescription_returnsCreatedApplication() {
    // Given
    String name = "Test App";

    // When
    OperationResult<Application> result = applicationService.createApplication(config, name, null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.isCreated()).isTrue();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getName()).isEqualTo(name);
    assertThat(result.getData().getDescription()).isNull();
  }

  @Test
  @DisplayName("createApplication - different names - generates unique IDs")
  void createApplication_differentNames_generatesUniqueIds() {
    // Given
    String name1 = "App One";
    String name2 = "App Two";

    // When
    OperationResult<Application> result1 =
        applicationService.createApplication(config, name1, null);
    OperationResult<Application> result2 =
        applicationService.createApplication(config, name2, null);

    // Then
    assertThat(result1.getData().getId()).isNotEqualTo(result2.getData().getId());
  }
}
