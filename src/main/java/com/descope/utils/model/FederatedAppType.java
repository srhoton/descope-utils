package com.descope.utils.model;

/**
 * Enumeration of federated application types supported by Descope.
 *
 * <p>Federated applications enable authentication through external identity providers using
 * standard protocols.
 */
public enum FederatedAppType {
  /** OpenID Connect (OIDC) federated application */
  OIDC,

  /** Security Assertion Markup Language (SAML) federated application */
  SAML;

  /**
   * Parses a string to a FederatedAppType enum value.
   *
   * @param type The type string to parse (case-insensitive)
   * @return The corresponding FederatedAppType
   * @throws IllegalArgumentException if the type is not recognized
   */
  public static FederatedAppType fromString(String type) {
    if (type == null) {
      throw new IllegalArgumentException("Federated app type cannot be null");
    }
    try {
      return FederatedAppType.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid federated app type: '"
              + type
              + "'. Valid types are: oidc, saml (case-insensitive)");
    }
  }
}
