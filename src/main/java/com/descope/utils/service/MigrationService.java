package com.descope.utils.service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.exception.DescopeException;
import com.descope.model.auth.AssociatedTenant;
import com.descope.model.user.request.BatchUserPasswordBcrypt;
import com.descope.model.user.request.BatchUserPasswordHashed;
import com.descope.model.user.request.BatchUserRequest;
import com.descope.model.user.response.UsersBatchResponse;
import com.descope.sdk.mgmt.UserService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.MigratedUser;
import com.descope.utils.model.OperationResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for migrating legacy users to Descope.
 *
 * <p>Provides operations to migrate users from legacy systems while preserving their existing
 * password hashes.
 */
@ApplicationScoped
public class MigrationService {

  private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

  private final DescopeService descopeService;

  /**
   * Creates a new MigrationService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public MigrationService(DescopeService descopeService) {
    this.descopeService = descopeService;
  }

  /**
   * Migrates a legacy user to Descope with their existing bcrypt password hash.
   *
   * <p>Creates a new user in the specified tenant with their roles and password hash preserved. The
   * user can immediately authenticate with their existing password without needing a reset.
   *
   * @param config The Descope configuration
   * @param email The user's email address (also used as login ID)
   * @param firstName The user's first name
   * @param lastName The user's last name
   * @param tenantId The tenant ID to associate the user with
   * @param roles List of role names to assign to the user in the tenant
   * @param bcryptHash The user's existing password in bcrypt format
   * @return OperationResult containing the migrated user details
   */
  public OperationResult<MigratedUser> migrateLegacyUser(
      DescopeConfig config,
      String email,
      String firstName,
      String lastName,
      String tenantId,
      List<String> roles,
      String bcryptHash) {
    logger.info("Migrating legacy user: {} to tenant: {}", email, tenantId);

    try {
      DescopeClient client = descopeService.createClient(config);
      UserService userService = client.getManagementServices().getUserService();

      // Build the bcrypt password hash structure
      BatchUserPasswordBcrypt bcrypt = BatchUserPasswordBcrypt.builder().hash(bcryptHash).build();

      BatchUserPasswordHashed hashedPassword =
          BatchUserPasswordHashed.builder().bcrypt(bcrypt).build();

      // Build tenant association with roles
      AssociatedTenant.AssociatedTenantBuilder tenantBuilder =
          AssociatedTenant.builder().tenantId(tenantId);

      if (roles != null && !roles.isEmpty()) {
        tenantBuilder.roleNames(roles);
      }

      // Build the display name
      String displayName = buildDisplayName(firstName, lastName);

      // Build the batch user request
      BatchUserRequest userRequest =
          BatchUserRequest.builder()
              .loginId(email)
              .email(email)
              .verifiedEmail(true)
              .givenName(firstName)
              .familyName(lastName)
              .displayName(displayName)
              .userTenants(Arrays.asList(tenantBuilder.build()))
              .hashedPassword(hashedPassword)
              .build();

      // Create the user via batch API (supports hashed passwords)
      UsersBatchResponse response = userService.createBatch(Arrays.asList(userRequest));

      // Check for failures
      if (response.getFailedUsers() != null && !response.getFailedUsers().isEmpty()) {
        String failureReason =
            response.getFailedUsers().get(0).getFailure() != null
                ? response.getFailedUsers().get(0).getFailure()
                : "Unknown error";
        logger.error("Failed to migrate user '{}': {}", email, failureReason);
        return OperationResult.failure("Failed to migrate user: " + failureReason);
      }

      // Extract the created user
      if (response.getCreatedUsers() == null || response.getCreatedUsers().isEmpty()) {
        logger.error("No user created for '{}'", email);
        return OperationResult.failure("No user was created");
      }

      String userId = response.getCreatedUsers().get(0).getUserId();

      MigratedUser migratedUser =
          new MigratedUser(userId, email, firstName, lastName, tenantId, roles, Instant.now());

      logger.info("Successfully migrated user: {} (ID: {}) to tenant: {}", email, userId, tenantId);
      return OperationResult.created(
          migratedUser, "User '" + email + "' migrated successfully to tenant '" + tenantId + "'");

    } catch (DescopeException e) {
      logger.error("Migration failed for user '{}': {}", email, e.getMessage());
      throw descopeService.wrapException("migrate user '" + email + "'", e);
    }
  }

  private String buildDisplayName(String firstName, String lastName) {
    StringBuilder sb = new StringBuilder();
    if (firstName != null && !firstName.isEmpty()) {
      sb.append(firstName);
    }
    if (lastName != null && !lastName.isEmpty()) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(lastName);
    }
    return sb.toString();
  }
}
