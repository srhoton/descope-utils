package com.descope.utils.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FederatedApplicationTest {

  @Test
  @DisplayName(
      "constructor - valid OIDC parameters - should create FederatedApplication with all fields")
  void constructor_validOidcParameters_shouldCreateFederatedApplication() {
    // Arrange
    String id = "fed-app-123";
    String name = "My OIDC App";
    String description = "Test OIDC application";
    FederatedAppType type = FederatedAppType.OIDC;
    String loginPageUrl = "https://example.com/login";
    Instant now = Instant.now();

    // Act
    FederatedApplication app =
        new FederatedApplication(id, name, description, type, loginPageUrl, now);

    // Assert
    assertNotNull(app);
    assertEquals(id, app.getId());
    assertEquals(name, app.getName());
    assertEquals(description, app.getDescription());
    assertEquals(type, app.getType());
    assertEquals(loginPageUrl, app.getLoginPageUrl());
    assertEquals(now, app.getCreatedAt());
  }

  @Test
  @DisplayName(
      "constructor - valid SAML parameters - should create FederatedApplication with SAML type")
  void constructor_validSamlParameters_shouldCreateFederatedApplication() {
    // Arrange
    String id = "fed-app-456";
    String name = "My SAML App";
    FederatedAppType type = FederatedAppType.SAML;
    Instant now = Instant.now();

    // Act
    FederatedApplication app = new FederatedApplication(id, name, null, type, null, now);

    // Assert
    assertNotNull(app);
    assertEquals(id, app.getId());
    assertEquals(name, app.getName());
    assertNull(app.getDescription());
    assertEquals(type, app.getType());
    assertNull(app.getLoginPageUrl());
    assertEquals(now, app.getCreatedAt());
  }

  @Test
  @DisplayName("constructor - null id - should throw NullPointerException")
  void constructor_nullId_shouldThrowException() {
    // Arrange
    Instant now = Instant.now();

    // Act & Assert
    assertThrows(
        NullPointerException.class,
        () ->
            new FederatedApplication(
                null, "name", "description", FederatedAppType.OIDC, "url", now));
  }

  @Test
  @DisplayName("constructor - null name - should throw NullPointerException")
  void constructor_nullName_shouldThrowException() {
    // Arrange
    Instant now = Instant.now();

    // Act & Assert
    assertThrows(
        NullPointerException.class,
        () ->
            new FederatedApplication("id", null, "description", FederatedAppType.OIDC, "url", now));
  }

  @Test
  @DisplayName("constructor - null type - should throw NullPointerException")
  void constructor_nullType_shouldThrowException() {
    // Arrange
    Instant now = Instant.now();

    // Act & Assert
    assertThrows(
        NullPointerException.class,
        () -> new FederatedApplication("id", "name", "description", null, "url", now));
  }

  @Test
  @DisplayName("equals - same object - should return true")
  void equals_sameObject_shouldReturnTrue() {
    // Arrange
    Instant now = Instant.now();
    FederatedApplication app =
        new FederatedApplication("id", "name", "description", FederatedAppType.OIDC, "url", now);

    // Act & Assert
    assertEquals(app, app);
  }

  @Test
  @DisplayName("equals - equal objects - should return true")
  void equals_equalObjects_shouldReturnTrue() {
    // Arrange
    Instant now = Instant.now();
    FederatedApplication app1 =
        new FederatedApplication("id", "name", "description", FederatedAppType.OIDC, "url", now);
    FederatedApplication app2 =
        new FederatedApplication("id", "name", "description", FederatedAppType.OIDC, "url", now);

    // Act & Assert
    assertEquals(app1, app2);
    assertEquals(app1.hashCode(), app2.hashCode());
  }

  @Test
  @DisplayName("equals - different id - should return false")
  void equals_differentId_shouldReturnFalse() {
    // Arrange
    Instant now = Instant.now();
    FederatedApplication app1 =
        new FederatedApplication("id1", "name", "description", FederatedAppType.OIDC, "url", now);
    FederatedApplication app2 =
        new FederatedApplication("id2", "name", "description", FederatedAppType.OIDC, "url", now);

    // Act & Assert
    assertNotEquals(app1, app2);
  }

  @Test
  @DisplayName("equals - different type - should return false")
  void equals_differentType_shouldReturnFalse() {
    // Arrange
    Instant now = Instant.now();
    FederatedApplication app1 =
        new FederatedApplication("id", "name", "description", FederatedAppType.OIDC, "url", now);
    FederatedApplication app2 =
        new FederatedApplication("id", "name", "description", FederatedAppType.SAML, "url", now);

    // Act & Assert
    assertNotEquals(app1, app2);
  }

  @Test
  @DisplayName("equals - null object - should return false")
  void equals_nullObject_shouldReturnFalse() {
    // Arrange
    Instant now = Instant.now();
    FederatedApplication app =
        new FederatedApplication("id", "name", "description", FederatedAppType.OIDC, "url", now);

    // Act & Assert
    assertNotEquals(app, null);
  }

  @Test
  @DisplayName("toString - valid object - should contain all fields")
  void toString_validObject_shouldContainAllFields() {
    // Arrange
    Instant now = Instant.now();
    FederatedApplication app =
        new FederatedApplication("id", "name", "description", FederatedAppType.OIDC, "url", now);

    // Act
    String result = app.toString();

    // Assert
    assertNotNull(result);
    assertTrue(result.contains("id"));
    assertTrue(result.contains("name"));
    assertTrue(result.contains("description"));
    assertTrue(result.contains("OIDC"));
    assertTrue(result.contains("url"));
  }

  @Test
  @DisplayName("FederatedAppType.fromString - valid oidc lowercase - should return OIDC")
  void federatedAppTypeFromString_validOidcLowercase_shouldReturnOidc() {
    // Act
    FederatedAppType result = FederatedAppType.fromString("oidc");

    // Assert
    assertEquals(FederatedAppType.OIDC, result);
  }

  @Test
  @DisplayName("FederatedAppType.fromString - valid OIDC uppercase - should return OIDC")
  void federatedAppTypeFromString_validOidcUppercase_shouldReturnOidc() {
    // Act
    FederatedAppType result = FederatedAppType.fromString("OIDC");

    // Assert
    assertEquals(FederatedAppType.OIDC, result);
  }

  @Test
  @DisplayName("FederatedAppType.fromString - valid saml lowercase - should return SAML")
  void federatedAppTypeFromString_validSamlLowercase_shouldReturnSaml() {
    // Act
    FederatedAppType result = FederatedAppType.fromString("saml");

    // Assert
    assertEquals(FederatedAppType.SAML, result);
  }

  @Test
  @DisplayName("FederatedAppType.fromString - invalid type - should throw IllegalArgumentException")
  void federatedAppTypeFromString_invalidType_shouldThrowException() {
    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> FederatedAppType.fromString("invalid"));
    assertTrue(exception.getMessage().contains("Invalid federated app type"));
    assertTrue(exception.getMessage().contains("oidc, saml"));
  }

  @Test
  @DisplayName("FederatedAppType.fromString - null type - should throw IllegalArgumentException")
  void federatedAppTypeFromString_nullType_shouldThrowException() {
    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> FederatedAppType.fromString(null));
    assertTrue(exception.getMessage().contains("cannot be null"));
  }
}
