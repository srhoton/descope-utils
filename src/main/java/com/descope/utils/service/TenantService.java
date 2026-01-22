package com.descope.utils.service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.exception.DescopeException;
import com.descope.model.tenant.Tenant;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing Descope tenants.
 *
 * <p>Provides operations to create tenants and associate them with applications, with idempotency
 * support.
 */
@ApplicationScoped
public class TenantService {

  private static final Logger logger = LoggerFactory.getLogger(TenantService.class);

  private final DescopeService descopeService;

  /**
   * Creates a new TenantService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public TenantService(DescopeService descopeService) {
    this.descopeService = descopeService;
  }

  /**
   * Creates a new Descope tenant with idempotency support.
   *
   * <p>If a tenant with the same name already exists, returns the existing tenant instead of
   * creating a duplicate. Note: Tenants are associated with applications through user assignments,
   * not direct associations.
   *
   * @param config The Descope configuration
   * @param name The tenant name
   * @param appId The application ID (not used for tenant creation, kept for interface
   *     compatibility)
   * @return OperationResult containing the created or existing tenant
   */
  public OperationResult<com.descope.utils.model.Tenant> createTenant(
      DescopeConfig config, String name, String appId) {
    logger.info("Creating tenant: {}", name);

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.TenantService sdkTenantService =
          client.getManagementServices().getTenantService();

      // Check if a tenant with the same name already exists
      List<Tenant> existingTenants = sdkTenantService.loadAll();
      for (Tenant tenant : existingTenants) {
        if (tenant.getName().equals(name)) {
          logger.info("Tenant '{}' already exists (ID: {})", name, tenant.getId());
          com.descope.utils.model.Tenant existing =
              new com.descope.utils.model.Tenant(
                  tenant.getId(), tenant.getName(), appId != null ? appId : "", Instant.now());
          return OperationResult.alreadyExists(existing, "Tenant '" + name + "' already exists");
        }
      }

      // Create new tenant with a custom ID based on the name (lowercase, no spaces)
      String tenantId = name.toLowerCase().replaceAll("\\s+", "-");
      sdkTenantService.createWithId(
          tenantId,
          name,
          Collections.emptyList(), // Self-provisioning domains (optional)
          new HashMap<>()); // Custom attributes (optional)

      com.descope.utils.model.Tenant newTenant =
          new com.descope.utils.model.Tenant(
              tenantId, name, appId != null ? appId : "", Instant.now());

      logger.info("Successfully created tenant: {} (ID: {})", name, tenantId);
      return OperationResult.created(newTenant, "Tenant '" + name + "' created successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("create tenant '" + name + "'", e);
    }
  }
}
