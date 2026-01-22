package com.descope.utils.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a Descope user.
 *
 * <p>A user is an individual identity within a tenant that can authenticate and access resources.
 */
public class User {

  private final String id;
  private final String loginId;
  private final String email;
  private final String tenantId;
  private final Instant createdAt;

  /**
   * Creates a new User instance.
   *
   * @param id The unique identifier for the user
   * @param loginId The login ID used for authentication
   * @param email The user's email address
   * @param tenantId The ID of the tenant this user belongs to
   * @param createdAt The timestamp when the user was created
   */
  public User(String id, String loginId, String email, String tenantId, Instant createdAt) {
    this.id = Objects.requireNonNull(id, "User ID cannot be null");
    this.loginId = Objects.requireNonNull(loginId, "Login ID cannot be null");
    this.email = email;
    this.tenantId = Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
    this.createdAt = createdAt;
  }

  /**
   * Gets the user ID.
   *
   * @return The user ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the login ID.
   *
   * @return The login ID
   */
  public String getLoginId() {
    return loginId;
  }

  /**
   * Gets the user's email address.
   *
   * @return The email address, or null if not set
   */
  public String getEmail() {
    return email;
  }

  /**
   * Gets the tenant ID this user belongs to.
   *
   * @return The tenant ID
   */
  public String getTenantId() {
    return tenantId;
  }

  /**
   * Gets the creation timestamp.
   *
   * @return The timestamp when the user was created, or null if not available
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
    User user = (User) o;
    return Objects.equals(id, user.id)
        && Objects.equals(loginId, user.loginId)
        && Objects.equals(email, user.email)
        && Objects.equals(tenantId, user.tenantId)
        && Objects.equals(createdAt, user.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, loginId, email, tenantId, createdAt);
  }

  @Override
  public String toString() {
    return "User{"
        + "id='"
        + id
        + '\''
        + ", loginId='"
        + loginId
        + '\''
        + ", email='"
        + email
        + '\''
        + ", tenantId='"
        + tenantId
        + '\''
        + ", createdAt="
        + createdAt
        + '}';
  }
}
