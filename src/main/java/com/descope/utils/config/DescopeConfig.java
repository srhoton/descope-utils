package com.descope.utils.config;

import java.util.Objects;

/**
 * Immutable configuration holder for Descope credentials.
 *
 * <p>Contains the project ID and management key required to interact with the Descope API.
 */
public class DescopeConfig {

  private final String projectId;
  private final String managementKey;
  private final CredentialSource source;

  /**
   * Creates a new DescopeConfig instance.
   *
   * @param projectId The Descope project ID
   * @param managementKey The Descope management key
   * @param source The source from which credentials were loaded
   */
  public DescopeConfig(String projectId, String managementKey, CredentialSource source) {
    this.projectId = Objects.requireNonNull(projectId, "Project ID cannot be null");
    this.managementKey = Objects.requireNonNull(managementKey, "Management key cannot be null");
    this.source = Objects.requireNonNull(source, "Credential source cannot be null");

    if (projectId.trim().isEmpty()) {
      throw new IllegalArgumentException("Project ID cannot be empty");
    }
    if (managementKey.trim().isEmpty()) {
      throw new IllegalArgumentException("Management key cannot be empty");
    }
  }

  /**
   * Gets the project ID.
   *
   * @return The Descope project ID
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Gets the management key.
   *
   * @return The Descope management key
   */
  public String getManagementKey() {
    return managementKey;
  }

  /**
   * Gets the credential source.
   *
   * @return The source from which credentials were loaded
   */
  public CredentialSource getSource() {
    return source;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DescopeConfig that = (DescopeConfig) o;
    return Objects.equals(projectId, that.projectId)
        && Objects.equals(managementKey, that.managementKey)
        && source == that.source;
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, managementKey, source);
  }

  @Override
  public String toString() {
    return "DescopeConfig{"
        + "projectId='"
        + projectId
        + '\''
        + ", managementKey='***'"
        + ", source="
        + source
        + '}';
  }
}
