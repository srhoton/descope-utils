package com.descope.utils.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.exception.DescopeException;
import com.descope.model.roles.RoleResponse;
import com.descope.sdk.mgmt.RolesService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.Role;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing RBAC roles in Descope.
 *
 * <p>Provides CRUD operations for roles at both project and tenant levels.
 */
@ApplicationScoped
public class RoleService {

  private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

  private final DescopeService descopeService;

  /**
   * Creates a new RoleService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public RoleService(DescopeService descopeService) {
    this.descopeService = descopeService;
  }

  /**
   * Creates a new role at project level.
   *
   * @param config The Descope configuration
   * @param name The role name
   * @param description The role description
   * @param permissionNames The list of permission names to associate with this role
   * @return OperationResult containing the created role
   */
  public OperationResult<Role> createRole(
      DescopeConfig config, String name, String description, List<String> permissionNames) {
    return createRole(config, name, null, description, permissionNames);
  }

  /**
   * Creates a new role, optionally at tenant level.
   *
   * @param config The Descope configuration
   * @param name The role name
   * @param tenantId The tenant ID for tenant-specific roles, null for project-level
   * @param description The role description
   * @param permissionNames The list of permission names to associate with this role
   * @return OperationResult containing the created role
   */
  public OperationResult<Role> createRole(
      DescopeConfig config,
      String name,
      String tenantId,
      String description,
      List<String> permissionNames) {
    String context = tenantId != null ? " in tenant: " + tenantId : " (project-level)";
    logger.info("Creating role: {}{}", name, context);

    try {
      DescopeClient client = descopeService.createClient(config);
      RolesService rolesService = client.getManagementServices().getRolesService();

      if (tenantId != null && !tenantId.isEmpty()) {
        rolesService.create(name, tenantId, description, permissionNames);
      } else {
        rolesService.create(name, description, permissionNames);
      }

      Role role = new Role(name, description, permissionNames, tenantId);

      logger.info("Successfully created role: {}{}", name, context);
      return OperationResult.created(role, "Role '" + name + "' created successfully" + context);

    } catch (DescopeException e) {
      logger.error("Failed to create role '{}': {}", name, e.getMessage());
      throw descopeService.wrapException("create role '" + name + "'", e);
    }
  }

  /**
   * Lists all roles.
   *
   * @param config The Descope configuration
   * @return OperationResult containing the list of roles
   */
  public OperationResult<List<Role>> listRoles(DescopeConfig config) {
    logger.info("Loading all roles");

    try {
      DescopeClient client = descopeService.createClient(config);
      RolesService rolesService = client.getManagementServices().getRolesService();

      RoleResponse response = rolesService.loadAll();

      List<Role> roles =
          response.getRoles().stream()
              .map(
                  r ->
                      new Role(
                          r.getName(), r.getDescription(), r.getPermissionNames(), r.getTenantId()))
              .collect(Collectors.toList());

      logger.info("Successfully loaded {} roles", roles.size());
      return OperationResult.success(roles, "Loaded " + roles.size() + " roles");

    } catch (DescopeException e) {
      logger.error("Failed to load roles: {}", e.getMessage());
      throw descopeService.wrapException("load roles", e);
    }
  }

  /**
   * Updates an existing role at project level.
   *
   * @param config The Descope configuration
   * @param name The current role name
   * @param newName The new role name (can be same as current)
   * @param description The new description
   * @param permissionNames The new list of permission names
   * @return OperationResult containing the updated role
   */
  public OperationResult<Role> updateRole(
      DescopeConfig config,
      String name,
      String newName,
      String description,
      List<String> permissionNames) {
    return updateRole(config, name, null, newName, description, permissionNames);
  }

  /**
   * Updates an existing role, optionally at tenant level.
   *
   * @param config The Descope configuration
   * @param name The current role name
   * @param tenantId The tenant ID for tenant-specific roles, null for project-level
   * @param newName The new role name (can be same as current)
   * @param description The new description
   * @param permissionNames The new list of permission names
   * @return OperationResult containing the updated role
   */
  public OperationResult<Role> updateRole(
      DescopeConfig config,
      String name,
      String tenantId,
      String newName,
      String description,
      List<String> permissionNames) {
    String context = tenantId != null ? " in tenant: " + tenantId : " (project-level)";
    logger.info("Updating role: {}{}", name, context);

    try {
      DescopeClient client = descopeService.createClient(config);
      RolesService rolesService = client.getManagementServices().getRolesService();

      if (tenantId != null && !tenantId.isEmpty()) {
        rolesService.update(name, tenantId, newName, description, permissionNames);
      } else {
        rolesService.update(name, newName, description, permissionNames);
      }

      Role role = new Role(newName, description, permissionNames, tenantId);

      logger.info("Successfully updated role: {} -> {}{}", name, newName, context);
      return OperationResult.success(role, "Role '" + name + "' updated successfully" + context);

    } catch (DescopeException e) {
      logger.error("Failed to update role '{}': {}", name, e.getMessage());
      throw descopeService.wrapException("update role '" + name + "'", e);
    }
  }

  /**
   * Deletes a role at project level.
   *
   * @param config The Descope configuration
   * @param name The role name to delete
   * @return OperationResult indicating success
   */
  public OperationResult<Void> deleteRole(DescopeConfig config, String name) {
    return deleteRole(config, name, null);
  }

  /**
   * Deletes a role, optionally at tenant level.
   *
   * @param config The Descope configuration
   * @param name The role name to delete
   * @param tenantId The tenant ID for tenant-specific roles, null for project-level
   * @return OperationResult indicating success
   */
  public OperationResult<Void> deleteRole(DescopeConfig config, String name, String tenantId) {
    String context = tenantId != null ? " from tenant: " + tenantId : " (project-level)";
    logger.info("Deleting role: {}{}", name, context);

    try {
      DescopeClient client = descopeService.createClient(config);
      RolesService rolesService = client.getManagementServices().getRolesService();

      if (tenantId != null && !tenantId.isEmpty()) {
        rolesService.delete(name, tenantId);
      } else {
        rolesService.delete(name);
      }

      logger.info("Successfully deleted role: {}{}", name, context);
      return OperationResult.success(null, "Role '" + name + "' deleted successfully" + context);

    } catch (DescopeException e) {
      logger.error("Failed to delete role '{}': {}", name, e.getMessage());
      throw descopeService.wrapException("delete role '" + name + "'", e);
    }
  }
}
