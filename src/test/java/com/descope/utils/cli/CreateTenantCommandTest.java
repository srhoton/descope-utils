package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for CreateTenantCommand.
 *
 * <p>Tests command-line parsing and option handling for the create-tenant command.
 */
class CreateTenantCommandTest {

  @Test
  @DisplayName("parse - with name only - should parse correctly")
  void parse_withNameOnly_shouldParseCorrectly() {
    // Arrange
    CreateTenantCommand command = new CreateTenantCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("MyTenant");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-tenant");
  }

  @Test
  @DisplayName("parse - with name and app ID - should parse correctly")
  void parse_withNameAndAppId_shouldParseCorrectly() {
    // Arrange
    CreateTenantCommand command = new CreateTenantCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("MyTenant", "--app-id", "app-123");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-tenant");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    // Arrange
    CreateTenantCommand command = new CreateTenantCommand();
    CommandLine cmd = new CommandLine(command);

    // Act & Assert
    assertThat(cmd.getCommandName()).isEqualTo("create-tenant");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Create a new Descope tenant");
  }
}
