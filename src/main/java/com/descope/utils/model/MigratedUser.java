package com.descope.utils.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user that has been migrated from a legacy system to Descope.
 *
 * <p>Contains the user's details and migration metadata.
 */
public class MigratedUser {

  private final String userId;
  private final String email;
  private final String firstName;
  private final String lastName;
  private final String tenantId;
  private final List<String> roles;
  private final Instant migratedAt;

  /**
   * Creates a new MigratedUser.
   *
   * @param userId The Descope user ID assigned after migration
   * @param email The user's email address
   * @param firstName The user's first name
   * @param lastName The user's last name
   * @param tenantId The tenant the user was migrated to
   * @param roles The roles assigned to the user
   * @param migratedAt The timestamp when the migration occurred
   */
  public MigratedUser(
      String userId,
      String email,
      String firstName,
      String lastName,
      String tenantId,
      List<String> roles,
      Instant migratedAt) {
    this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.firstName = firstName;
    this.lastName = lastName;
    this.tenantId = tenantId;
    this.roles = roles;
    this.migratedAt = migratedAt;
  }

  /**
   * Gets the Descope user ID.
   *
   * @return The user ID
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Gets the user's email address.
   *
   * @return The email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Gets the user's first name.
   *
   * @return The first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Gets the user's last name.
   *
   * @return The last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Gets the tenant ID the user was migrated to.
   *
   * @return The tenant ID
   */
  public String getTenantId() {
    return tenantId;
  }

  /**
   * Gets the roles assigned to the user.
   *
   * @return The list of role names
   */
  public List<String> getRoles() {
    return roles;
  }

  /**
   * Gets the timestamp when the migration occurred.
   *
   * @return The migration timestamp
   */
  public Instant getMigratedAt() {
    return migratedAt;
  }

  @Override
  public String toString() {
    return "MigratedUser{"
        + "userId='"
        + userId
        + '\''
        + ", email='"
        + email
        + '\''
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", tenantId='"
        + tenantId
        + '\''
        + ", roles="
        + roles
        + ", migratedAt="
        + migratedAt
        + '}';
  }
}
