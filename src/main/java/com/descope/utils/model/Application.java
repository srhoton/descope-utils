package com.descope.utils.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a Descope application.
 *
 * <p>An application is a container for tenants and defines the scope of authentication and
 * authorization.
 */
public class Application {

  private final String id;
  private final String name;
  private final String description;
  private final Instant createdAt;

  /**
   * Creates a new Application instance.
   *
   * @param id The unique identifier for the application
   * @param name The application name
   * @param description The application description
   * @param createdAt The timestamp when the application was created
   */
  public Application(String id, String name, String description, Instant createdAt) {
    this.id = Objects.requireNonNull(id, "Application ID cannot be null");
    this.name = Objects.requireNonNull(name, "Application name cannot be null");
    this.description = description;
    this.createdAt = createdAt;
  }

  /**
   * Gets the application ID.
   *
   * @return The application ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the application name.
   *
   * @return The application name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the application description.
   *
   * @return The application description, or null if not set
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the creation timestamp.
   *
   * @return The timestamp when the application was created, or null if not available
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
    Application that = (Application) o;
    return Objects.equals(id, that.id)
        && Objects.equals(name, that.name)
        && Objects.equals(description, that.description)
        && Objects.equals(createdAt, that.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, createdAt);
  }

  @Override
  public String toString() {
    return "Application{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", createdAt="
        + createdAt
        + '}';
  }
}
