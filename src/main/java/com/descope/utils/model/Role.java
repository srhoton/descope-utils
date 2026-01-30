package com.descope.utils.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents an RBAC role in Descope.
 *
 * <p>Roles can be project-level (shared across all tenants) or tenant-specific.
 */
public class Role {

  private final String name;
  private final String description;
  private final List<String> permissionNames;
  private final String tenantId;

  /**
   * Creates a new Role.
   *
   * @param name The role name
   * @param description The role description
   * @param permissionNames The list of permission names associated with this role
   * @param tenantId The tenant ID if this is a tenant-specific role, null for project-level roles
   */
  public Role(String name, String description, List<String> permissionNames, String tenantId) {
    this.name = Objects.requireNonNull(name, "Role name cannot be null");
    this.description = description;
    this.permissionNames = permissionNames;
    this.tenantId = tenantId;
  }

  /**
   * Gets the role name.
   *
   * @return The role name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the role description.
   *
   * @return The role description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the permission names associated with this role.
   *
   * @return The list of permission names
   */
  public List<String> getPermissionNames() {
    return permissionNames;
  }

  /**
   * Gets the tenant ID for tenant-specific roles.
   *
   * @return The tenant ID, or null for project-level roles
   */
  public String getTenantId() {
    return tenantId;
  }

  /**
   * Checks if this is a tenant-specific role.
   *
   * @return true if this role is tenant-specific, false if project-level
   */
  public boolean isTenantRole() {
    return tenantId != null && !tenantId.isEmpty();
  }

  @Override
  public String toString() {
    return "Role{"
        + "name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", permissionNames="
        + permissionNames
        + ", tenantId='"
        + tenantId
        + '\''
        + '}';
  }
}
