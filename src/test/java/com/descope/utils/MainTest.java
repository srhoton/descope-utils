package com.descope.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;

/**
 * Tests for the Main application entry point.
 *
 * <p>These tests verify that the Quarkus Command Mode integration works correctly with Picocli
 * commands.
 */
@QuarkusMainTest
public class MainTest {

  @Test
  @Launch({})
  @DisplayName("main - no arguments - should display help message")
  void main_noArguments_shouldDisplayHelpMessage(LaunchResult result) {
    // Assert
    assertThat(result.exitCode()).isZero();
    assertThat(result.getOutput())
        .contains("Please specify a subcommand")
        .contains("Use --help to see available commands");
  }

  @Test
  @Launch({"--help"})
  @DisplayName("main - help flag - should display usage information")
  void main_helpFlag_shouldDisplayUsageInformation(LaunchResult result) {
    // Assert
    assertThat(result.exitCode()).isZero();
    assertThat(result.getOutput())
        .contains("descope-utils")
        .contains("Command-line utilities for managing Descope resources");
  }

  @Test
  @Launch({"create-app", "--help"})
  @DisplayName("main - create-app help - should display create-app command help")
  void main_createAppHelp_shouldDisplayCreateAppHelp(LaunchResult result) {
    // Assert
    assertThat(result.exitCode()).isZero();
    assertThat(result.getOutput())
        .contains("create-app")
        .contains("Create a new Descope application");
  }

  @Test
  @Launch({"create-tenant", "--help"})
  @DisplayName("main - create-tenant help - should display create-tenant command help")
  void main_createTenantHelp_shouldDisplayCreateTenantHelp(LaunchResult result) {
    // Assert
    assertThat(result.exitCode()).isZero();
    assertThat(result.getOutput())
        .contains("create-tenant")
        .contains("Create a new Descope tenant");
  }

  @Test
  @Launch({"create-user", "--help"})
  @DisplayName("main - create-user help - should display create-user command help")
  void main_createUserHelp_shouldDisplayCreateUserHelp(LaunchResult result) {
    // Assert
    assertThat(result.exitCode()).isZero();
    assertThat(result.getOutput()).contains("create-user").contains("Create a new Descope user");
  }

  // Note: Error case tests (invalid commands, missing args) are omitted here because
  // @Launch annotation expects successful execution (exit code 0) by default.
  // These error scenarios are better tested in integration tests where we can
  // capture actual process exit codes.
}
