package com.descope.utils.service;

import java.time.Instant;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.exception.DescopeException;
import com.descope.model.auth.AssociatedTenant;
import com.descope.model.user.request.UserRequest;
import com.descope.model.user.response.UserResponseDetails;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing Descope users.
 *
 * <p>Provides operations to create users within tenants with idempotency support.
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
   * @param config The Descope configuration
   * @param loginId The login ID for the user
   * @param email The user's email address (optional)
   * @param tenantId The tenant ID the user belongs to
   * @return OperationResult containing the created or existing user
   */
  public OperationResult<User> createUser(
      DescopeConfig config, String loginId, String email, String tenantId) {
    logger.info("Creating user: {} in tenant: {}", loginId, tenantId);

    try {
      DescopeClient client = descopeService.createClient(config);
      com.descope.sdk.mgmt.UserService sdkUserService =
          client.getManagementServices().getUserService();

      // Check if user already exists
      try {
        UserResponseDetails existingUserDetails = sdkUserService.load(loginId);
        com.descope.model.user.response.UserResponse existingUser = existingUserDetails.getUser();
        logger.info("User '{}' already exists (ID: {})", loginId, existingUser.getUserId());

        User existing =
            new User(
                existingUser.getUserId(),
                loginId,
                existingUser.getEmail() != null ? existingUser.getEmail() : "",
                tenantId != null ? tenantId : "",
                Instant.now());

        return OperationResult.alreadyExists(existing, "User '" + loginId + "' already exists");
      } catch (DescopeException e) {
        // User doesn't exist, continue to create
        if (!e.getMessage().contains("not found") && !e.getMessage().contains("404")) {
          // Some other error occurred
          throw e;
        }
      }

      // Create new user
      UserRequest.UserRequestBuilder requestBuilder =
          UserRequest.builder().email(email != null ? email : loginId);

      // Add tenant association if provided
      if (tenantId != null && !tenantId.isEmpty()) {
        requestBuilder.userTenants(
            Arrays.asList(AssociatedTenant.builder().tenantId(tenantId).build()));
      }

      UserResponseDetails createdUserDetails =
          sdkUserService.create(loginId, requestBuilder.build());
      com.descope.model.user.response.UserResponse createdUser = createdUserDetails.getUser();

      User user =
          new User(
              createdUser.getUserId(),
              loginId,
              createdUser.getEmail() != null ? createdUser.getEmail() : "",
              tenantId != null ? tenantId : "",
              Instant.now());

      logger.info("Successfully created user: {} (ID: {})", loginId, user.getId());
      return OperationResult.created(user, "User '" + loginId + "' created successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("create user '" + loginId + "'", e);
    }
  }
}
