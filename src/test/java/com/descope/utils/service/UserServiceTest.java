package com.descope.utils.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.config.CredentialSource;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.User;

/**
 * Unit tests for UserService stub implementation.
 *
 * <p>NOTE: These tests verify the stub implementation. Full SDK integration tests will be in the
 * integration test suite.
 */
class UserServiceTest {

  private DescopeService descopeService;
  private UserService userService;

  @BeforeEach
  void setUp() {
    DescopeConfig config =
        new DescopeConfig("test-project-id", "test-management-key", CredentialSource.COMMAND_LINE);
    descopeService = new DescopeService(config);
    userService = new UserService(descopeService);
  }

  @Test
  @DisplayName("createUser - valid loginId with email and tenantId - returns created user")
  void createUser_validLoginIdWithEmailAndTenantId_returnsCreatedUser() {
    // Given
    String loginId = "user@example.com";
    String email = "user@example.com";
    String tenantId = "tenant-123";

    // When
    OperationResult<User> result = userService.createUser(loginId, email, tenantId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.isCreated()).isTrue();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getLoginId()).isEqualTo(loginId);
    assertThat(result.getData().getEmail()).isEqualTo(email);
    assertThat(result.getData().getTenantId()).isEqualTo(tenantId);
    assertThat(result.getData().getId()).isNotNull();
    assertThat(result.getMessage()).contains("created successfully");
  }

  @Test
  @DisplayName("createUser - valid loginId without email - returns created user")
  void createUser_validLoginIdWithoutEmail_returnsCreatedUser() {
    // Given
    String loginId = "user123";
    String tenantId = "tenant-123";

    // When
    OperationResult<User> result = userService.createUser(loginId, null, tenantId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.isCreated()).isTrue();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getLoginId()).isEqualTo(loginId);
    assertThat(result.getData().getEmail()).isNull();
  }

  @Test
  @DisplayName("createUser - valid loginId without tenantId - returns created user")
  void createUser_validLoginIdWithoutTenantId_returnsCreatedUser() {
    // Given
    String loginId = "user@example.com";
    String email = "user@example.com";

    // When
    OperationResult<User> result = userService.createUser(loginId, email, null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.isCreated()).isTrue();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getTenantId()).isEmpty();
  }

  @Test
  @DisplayName("createUser - different loginIds - generates unique IDs")
  void createUser_differentLoginIds_generatesUniqueIds() {
    // Given
    String loginId1 = "user1@example.com";
    String loginId2 = "user2@example.com";

    // When
    OperationResult<User> result1 = userService.createUser(loginId1, loginId1, "tenant-1");
    OperationResult<User> result2 = userService.createUser(loginId2, loginId2, "tenant-1");

    // Then
    assertThat(result1.getData().getId()).isNotEqualTo(result2.getData().getId());
  }
}
