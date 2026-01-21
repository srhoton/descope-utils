package com.descope.utils.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a Descope tenant.
 *
 * <p>A tenant is an organizational unit within an application that isolates users and their data.
 */
public class Tenant {

  private final String id;
  private final String name;
  private final String appId;
  private final Instant createdAt;

  /**
   * Creates a new Tenant instance.
   *
   * @param id The unique identifier for the tenant
   * @param name The tenant name
   * @param appId The ID of the application this tenant belongs to
   * @param createdAt The timestamp when the tenant was created
   */
  public Tenant(String id, String name, String appId, Instant createdAt) {
    this.id = Objects.requireNonNull(id, "Tenant ID cannot be null");
    this.name = Objects.requireNonNull(name, "Tenant name cannot be null");
    this.appId = Objects.requireNonNull(appId, "Application ID cannot be null");
    this.createdAt = createdAt;
  }

  /**
   * Gets the tenant ID.
   *
   * @return The tenant ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the tenant name.
   *
   * @return The tenant name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the application ID this tenant belongs to.
   *
   * @return The application ID
   */
  public String getAppId() {
    return appId;
  }

  /**
   * Gets the creation timestamp.
   *
   * @return The timestamp when the tenant was created, or null if not available
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
    Tenant tenant = (Tenant) o;
    return Objects.equals(id, tenant.id)
        && Objects.equals(name, tenant.name)
        && Objects.equals(appId, tenant.appId)
        && Objects.equals(createdAt, tenant.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, appId, createdAt);
  }

  @Override
  public String toString() {
    return "Tenant{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", appId='"
        + appId
        + '\''
        + ", createdAt="
        + createdAt
        + '}';
  }
}
