package com.descope.utils.cli;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.model.FederatedAppType;

/**
 * Unit tests for CreateFederatedAppCommand.
 *
 * <p>These tests verify the command structure and basic validation logic. Full integration tests
 * with Picocli and CDI are handled by the main application tests.
 */
public class CreateFederatedAppCommandTest {

  @Test
  @DisplayName("constructor - should create command instance")
  void constructor_shouldCreateCommandInstance() {
    // Act
    CreateFederatedAppCommand command = new CreateFederatedAppCommand();

    // Assert
    assertNotNull(command);
  }

  @Test
  @DisplayName("FederatedAppType validation - valid types - should accept oidc and saml")
  void federatedAppTypeValidation_validTypes_shouldAcceptOidcAndSaml() {
    // Act & Assert
    assertDoesNotThrow(() -> FederatedAppType.fromString("oidc"));
    assertDoesNotThrow(() -> FederatedAppType.fromString("OIDC"));
    assertDoesNotThrow(() -> FederatedAppType.fromString("saml"));
    assertDoesNotThrow(() -> FederatedAppType.fromString("SAML"));
  }

  @Test
  @DisplayName("FederatedAppType validation - invalid type - should throw exception")
  void federatedAppTypeValidation_invalidType_shouldThrowException() {
    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> FederatedAppType.fromString("invalid"));
    assertTrue(exception.getMessage().contains("Invalid federated app type"));
  }

  @Test
  @DisplayName("FederatedAppType - default should be OIDC")
  void federatedAppType_defaultShouldBeOidc() {
    // This test documents that the default type in the command is "oidc"
    // which maps to FederatedAppType.OIDC
    FederatedAppType defaultType = FederatedAppType.fromString("oidc");
    assertEquals(FederatedAppType.OIDC, defaultType);
  }
}
