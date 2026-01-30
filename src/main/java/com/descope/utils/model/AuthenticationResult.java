package com.descope.utils.model;

import java.util.Objects;

/**
 * Represents the result of a user authentication operation.
 *
 * <p>Contains the session JWT, refresh JWT, and user information returned from successful
 * authentication.
 */
public class AuthenticationResult {

  private final String sessionJwt;
  private final String refreshJwt;
  private final String userId;
  private final String loginId;
  private final long expiresAt;

  /**
   * Creates a new AuthenticationResult.
   *
   * @param sessionJwt The session JWT token
   * @param refreshJwt The refresh JWT token
   * @param userId The authenticated user's ID
   * @param loginId The login ID used for authentication
   * @param expiresAt The expiration timestamp of the session token
   */
  public AuthenticationResult(
      String sessionJwt, String refreshJwt, String userId, String loginId, long expiresAt) {
    this.sessionJwt = Objects.requireNonNull(sessionJwt, "Session JWT cannot be null");
    this.refreshJwt = refreshJwt;
    this.userId = userId;
    this.loginId = loginId;
    this.expiresAt = expiresAt;
  }

  /**
   * Gets the session JWT token.
   *
   * @return The session JWT
   */
  public String getSessionJwt() {
    return sessionJwt;
  }

  /**
   * Gets the refresh JWT token.
   *
   * @return The refresh JWT, or null if not provided
   */
  public String getRefreshJwt() {
    return refreshJwt;
  }

  /**
   * Gets the authenticated user's ID.
   *
   * @return The user ID
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Gets the login ID used for authentication.
   *
   * @return The login ID
   */
  public String getLoginId() {
    return loginId;
  }

  /**
   * Gets the expiration timestamp of the session token.
   *
   * @return The expiration timestamp in seconds since epoch
   */
  public long getExpiresAt() {
    return expiresAt;
  }

  @Override
  public String toString() {
    return "AuthenticationResult{"
        + "userId='"
        + userId
        + '\''
        + ", loginId='"
        + loginId
        + '\''
        + ", expiresAt="
        + expiresAt
        + ", sessionJwt='"
        + (sessionJwt != null ? "[PRESENT]" : "[ABSENT]")
        + '\''
        + ", refreshJwt='"
        + (refreshJwt != null ? "[PRESENT]" : "[ABSENT]")
        + '\''
        + '}';
  }
}
