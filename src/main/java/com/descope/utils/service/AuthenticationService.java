package com.descope.utils.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.exception.DescopeException;
import com.descope.model.auth.AuthenticationInfo;
import com.descope.model.user.User;
import com.descope.sdk.auth.PasswordService;
import com.descope.sdk.mgmt.UserService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.AuthenticationResult;
import com.descope.utils.model.OperationResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for authenticating users via Descope.
 *
 * <p>Provides headless authentication operations including password-based sign-in.
 */
@ApplicationScoped
public class AuthenticationService {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

  private final DescopeService descopeService;

  /**
   * Creates a new AuthenticationService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public AuthenticationService(DescopeService descopeService) {
    this.descopeService = descopeService;
  }

  /**
   * Authenticates a user with login ID and password.
   *
   * <p>Performs headless password authentication and returns the session JWT and refresh JWT upon
   * success.
   *
   * @param config The Descope configuration
   * @param loginId The user's login ID (email, phone, or username)
   * @param password The user's password
   * @return OperationResult containing the authentication result with JWTs
   */
  public OperationResult<AuthenticationResult> authenticateWithPassword(
      DescopeConfig config, String loginId, String password) {
    logger.info("Authenticating user: {}", loginId);

    try {
      DescopeClient client = descopeService.createClient(config);
      PasswordService passwordService = client.getAuthenticationServices().getPasswordService();

      // Perform password sign-in
      AuthenticationInfo authInfo = passwordService.signIn(loginId, password);

      // Extract tokens and user info
      String sessionJwt = authInfo.getToken().getJwt();
      String refreshJwt =
          authInfo.getRefreshToken() != null ? authInfo.getRefreshToken().getJwt() : null;
      String userId = authInfo.getToken().getClaims().get("sub").toString();
      long expiresAt =
          authInfo.getToken().getClaims().get("exp") != null
              ? ((Number) authInfo.getToken().getClaims().get("exp")).longValue()
              : 0L;

      AuthenticationResult result =
          new AuthenticationResult(sessionJwt, refreshJwt, userId, loginId, expiresAt);

      logger.info("Successfully authenticated user: {} (ID: {})", loginId, userId);
      return OperationResult.success(result, "Authentication successful for '" + loginId + "'");

    } catch (DescopeException e) {
      logger.error("Authentication failed for user '{}': {}", loginId, e.getMessage());
      throw descopeService.wrapException("authenticate user '" + loginId + "'", e);
    }
  }

  /**
   * Signs up a new user with login ID and password.
   *
   * <p>Creates a new user with password authentication and returns the session JWT and refresh JWT
   * upon success.
   *
   * @param config The Descope configuration
   * @param loginId The user's login ID (email, phone, or username)
   * @param password The user's password (must meet configured requirements)
   * @param name The user's display name (optional)
   * @param email The user's email address (optional, defaults to loginId if not provided)
   * @param phone The user's phone number (optional)
   * @return OperationResult containing the authentication result with JWTs
   */
  public OperationResult<AuthenticationResult> signUpWithPassword(
      DescopeConfig config,
      String loginId,
      String password,
      String name,
      String email,
      String phone) {
    logger.info("Signing up user: {}", loginId);

    try {
      DescopeClient client = descopeService.createClient(config);
      PasswordService passwordService = client.getAuthenticationServices().getPasswordService();

      // Build user object
      User.UserBuilder userBuilder = User.builder();
      if (name != null && !name.isEmpty()) {
        userBuilder.name(name);
      }
      if (email != null && !email.isEmpty()) {
        userBuilder.email(email);
      } else {
        // Default email to loginId if it looks like an email
        if (loginId.contains("@")) {
          userBuilder.email(loginId);
        }
      }
      if (phone != null && !phone.isEmpty()) {
        userBuilder.phone(phone);
      }
      User user = userBuilder.build();

      // Perform password sign-up
      AuthenticationInfo authInfo = passwordService.signUp(loginId, user, password);

      // Extract tokens and user info
      String sessionJwt = authInfo.getToken().getJwt();
      String refreshJwt =
          authInfo.getRefreshToken() != null ? authInfo.getRefreshToken().getJwt() : null;
      String userId = authInfo.getToken().getClaims().get("sub").toString();
      long expiresAt =
          authInfo.getToken().getClaims().get("exp") != null
              ? ((Number) authInfo.getToken().getClaims().get("exp")).longValue()
              : 0L;

      AuthenticationResult result =
          new AuthenticationResult(sessionJwt, refreshJwt, userId, loginId, expiresAt);

      logger.info("Successfully signed up user: {} (ID: {})", loginId, userId);
      return OperationResult.created(result, "Sign-up successful for '" + loginId + "'");

    } catch (DescopeException e) {
      logger.error("Sign-up failed for user '{}': {}", loginId, e.getMessage());
      throw descopeService.wrapException("sign up user '" + loginId + "'", e);
    }
  }

  /**
   * Sets an active password for an existing user.
   *
   * <p>This allows an administrator to set a permanent password for a user. The user can
   * immediately authenticate with this password without being forced to change it.
   *
   * @param config The Descope configuration
   * @param loginId The user's login ID (email, phone, or username)
   * @param password The new password to set (must meet configured requirements)
   * @return OperationResult indicating success or failure
   */
  public OperationResult<Void> setActivePassword(
      DescopeConfig config, String loginId, String password) {
    logger.info("Setting active password for user: {}", loginId);

    try {
      DescopeClient client = descopeService.createClient(config);
      UserService userService = client.getManagementServices().getUserService();

      userService.setActivePassword(loginId, password);

      logger.info("Successfully set active password for user: {}", loginId);
      return OperationResult.success(null, "Active password set for '" + loginId + "'");

    } catch (DescopeException e) {
      logger.error("Failed to set active password for user '{}': {}", loginId, e.getMessage());
      throw descopeService.wrapException("set active password for user '" + loginId + "'", e);
    }
  }

  /**
   * Sets a temporary password for an existing user.
   *
   * <p>This allows an administrator to set a temporary password for a user. The user will be
   * required to change their password on their next authentication.
   *
   * @param config The Descope configuration
   * @param loginId The user's login ID (email, phone, or username)
   * @param password The temporary password to set (must meet configured requirements)
   * @return OperationResult indicating success or failure
   */
  public OperationResult<Void> setTemporaryPassword(
      DescopeConfig config, String loginId, String password) {
    logger.info("Setting temporary password for user: {}", loginId);

    try {
      DescopeClient client = descopeService.createClient(config);
      UserService userService = client.getManagementServices().getUserService();

      userService.setTemporaryPassword(loginId, password);

      logger.info("Successfully set temporary password for user: {}", loginId);
      return OperationResult.success(
          null, "Temporary password set for '" + loginId + "' (user must change on next login)");

    } catch (DescopeException e) {
      logger.error("Failed to set temporary password for user '{}': {}", loginId, e.getMessage());
      throw descopeService.wrapException("set temporary password for user '" + loginId + "'", e);
    }
  }
}
