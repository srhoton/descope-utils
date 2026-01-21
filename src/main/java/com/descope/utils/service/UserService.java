package com.descope.utils.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.model.OperationResult;
import com.descope.utils.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing Descope users.
 *
 * <p>Provides operations to create users within tenants with idempotency support.
 *
 * <p>NOTE: This is a simplified stub implementation. Full SDK integration will be completed during
 * integration testing phase.
 */
@ApplicationScoped
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final DescopeService descopeService;

  /**
   * Creates a new UserService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public UserService(DescopeService descopeService) {
    this.descopeService = descopeService;
  }

  /**
   * Creates a new Descope user with idempotency support.
   *
   * <p>If a user with the same login ID already exists, returns the existing user instead of
   * creating a duplicate.
   *
   * @param loginId The login ID for the user
   * @param email The user's email address (optional)
   * @param tenantId The tenant ID the user belongs to
   * @return OperationResult containing the created or existing user
   */
  public OperationResult<User> createUser(String loginId, String email, String tenantId) {
    logger.info("Creating user: {} in tenant: {}", loginId, tenantId);

    try {
      // TODO: Implement actual SDK integration
      // Example pseudocode:
      // DescopeClient client = descopeService.createClient();
      // Try to load user: UserResponse existing = client.manageUser(...).load(loginId);
      // If not found, create: UserRequest request = UserRequest.builder()
      //   .loginId(loginId).email(email).userTenants(List.of(tenantId)).build();
      // UserResponse created = client.manageUser(...).create(request);

      // For now, create a placeholder user
      User user =
          new User(
              "user-" + loginId.hashCode(),
              loginId,
              email,
              tenantId != null ? tenantId : "",
              Instant.now());

      logger.info("Successfully created user: {} (ID: {})", loginId, user.getId());
      return OperationResult.created(user, "User '" + loginId + "' created successfully");

    } catch (Exception e) {
      throw descopeService.wrapException("create user '" + loginId + "'", e);
    }
  }
}
