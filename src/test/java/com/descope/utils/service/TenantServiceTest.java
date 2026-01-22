package com.descope.utils.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.config.CredentialSource;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.Tenant;

/**
 * Unit tests for TenantService SDK integration.
 *
 * <p>NOTE: These tests require valid Descope credentials and network access. They are disabled by
 * default. Integration tests will be in the integration test suite.
 */
@org.junit.jupiter.api.Disabled("Requires real Descope credentials and network access")
class TenantServiceTest {

  private DescopeService descopeService;
  private TenantService tenantService;
  private DescopeConfig config;

  @BeforeEach
  void setUp() {
    config =
        new DescopeConfig("test-project-id", "test-management-key", CredentialSource.COMMAND_LINE);
    descopeService = new DescopeService();
    tenantService = new TenantService(descopeService);
  }

  @Test
  @DisplayName("createTenant - valid name with appId - returns created tenant")
  void createTenant_validNameWithAppId_returnsCreatedTenant() {
    // Given
    String name = "Test Tenant";
    String appId = "app-123";

    // When
    OperationResult<Tenant> result = tenantService.createTenant(config, name, appId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.isCreated()).isTrue();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getName()).isEqualTo(name);
    assertThat(result.getData().getAppId()).isEqualTo(appId);
    assertThat(result.getData().getId()).isNotNull();
    assertThat(result.getMessage()).contains("created successfully");
  }

  @Test
  @DisplayName("createTenant - valid name without appId - returns created tenant")
  void createTenant_validNameWithoutAppId_returnsCreatedTenant() {
    // Given
    String name = "Test Tenant";

    // When
    OperationResult<Tenant> result = tenantService.createTenant(config, name, null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.isCreated()).isTrue();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getName()).isEqualTo(name);
    assertThat(result.getData().getAppId()).isEmpty();
  }

  @Test
  @DisplayName("createTenant - different names - generates unique IDs")
  void createTenant_differentNames_generatesUniqueIds() {
    // Given
    String name1 = "Tenant One";
    String name2 = "Tenant Two";

    // When
    OperationResult<Tenant> result1 = tenantService.createTenant(config, name1, null);
    OperationResult<Tenant> result2 = tenantService.createTenant(config, name2, null);

    // Then
    assertThat(result1.getData().getId()).isNotEqualTo(result2.getData().getId());
  }
}
