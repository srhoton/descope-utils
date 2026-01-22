package com.descope.utils.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.model.FederatedAppType;

/**
 * Unit tests for FederatedApplicationService.
 *
 * <p>Note: These tests verify the service structure and basic logic. Full integration tests with
 * the real Descope SDK require valid credentials and should be run separately.
 */
public class FederatedApplicationServiceTest {

  @Test
  @DisplayName("constructor - valid DescopeService - should create service instance")
  void constructor_validDescopeService_shouldCreateService() {
    // Arrange
    DescopeService descopeService = new DescopeService();

    // Act
    FederatedApplicationService service = new FederatedApplicationService(descopeService);

    // Assert
    assertNotNull(service);
  }

  @Test
  @DisplayName("FederatedAppType - should have OIDC and SAML types")
  void federatedAppType_shouldHaveOidcAndSamlTypes() {
    // Assert
    assertEquals(2, FederatedAppType.values().length);
    assertNotNull(FederatedAppType.OIDC);
    assertNotNull(FederatedAppType.SAML);
  }

  @Test
  @DisplayName("FederatedAppType.fromString - valid types - should parse correctly")
  void federatedAppTypeFromString_validTypes_shouldParseCorrectly() {
    // Act & Assert
    assertEquals(FederatedAppType.OIDC, FederatedAppType.fromString("oidc"));
    assertEquals(FederatedAppType.OIDC, FederatedAppType.fromString("OIDC"));
    assertEquals(FederatedAppType.SAML, FederatedAppType.fromString("saml"));
    assertEquals(FederatedAppType.SAML, FederatedAppType.fromString("SAML"));
  }
}
