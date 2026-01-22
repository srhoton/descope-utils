package com.descope.utils.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a Descope federated application (OIDC or SAML).
 *
 * <p>A federated application enables authentication through external identity providers using OIDC
 * or SAML protocols. It contains configuration such as name, description, login page URL, and
 * application-specific identifiers.
 */
public class FederatedApplication {

  private final String id;
  private final String name;
  private final String description;
  private final FederatedAppType type;
  private final String loginPageUrl;
  private final Instant createdAt;

  /**
   * Creates a new FederatedApplication instance.
   *
   * @param id The unique identifier for the federated application
   * @param name The application name
   * @param description The application description (can be null)
   * @param type The federated application type (OIDC or SAML)
   * @param loginPageUrl The login page URL (can be null)
   * @param createdAt The timestamp when the application was created
   */
  public FederatedApplication(
      String id,
      String name,
      String description,
      FederatedAppType type,
      String loginPageUrl,
      Instant createdAt) {
    this.id = Objects.requireNonNull(id, "Federated application ID cannot be null");
    this.name = Objects.requireNonNull(name, "Federated application name cannot be null");
    this.description = description;
    this.type = Objects.requireNonNull(type, "Federated application type cannot be null");
    this.loginPageUrl = loginPageUrl;
    this.createdAt = createdAt;
  }

  /**
   * Gets the federated application ID.
   *
   * @return The federated application ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the federated application name.
   *
   * @return The federated application name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the federated application description.
   *
   * @return The federated application description, or null if not set
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the federated application type.
   *
   * @return The federated application type (OIDC or SAML)
   */
  public FederatedAppType getType() {
    return type;
  }

  /**
   * Gets the login page URL.
   *
   * @return The login page URL, or null if not set
   */
  public String getLoginPageUrl() {
    return loginPageUrl;
  }

  /**
   * Gets the creation timestamp.
   *
   * @return The timestamp when the federated application was created, or null if not available
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FederatedApplication that = (FederatedApplication) o;
    return Objects.equals(id, that.id)
        && Objects.equals(name, that.name)
        && Objects.equals(description, that.description)
        && type == that.type
        && Objects.equals(loginPageUrl, that.loginPageUrl)
        && Objects.equals(createdAt, that.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, type, loginPageUrl, createdAt);
  }

  @Override
  public String toString() {
    return "FederatedApplication{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", type="
        + type
        + ", loginPageUrl='"
        + loginPageUrl
        + '\''
        + ", createdAt="
        + createdAt
        + '}';
  }
}
