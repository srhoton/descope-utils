package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for CreateUserCommand.
 *
 * <p>Tests command-line parsing and option handling for the create-user command.
 */
class CreateUserCommandTest {

  @Test
  @DisplayName("parse - with loginId and tenant ID - should parse correctly")
  void parse_withLoginIdAndTenantId_shouldParseCorrectly() {
    // Arrange
    CreateUserCommand command = new CreateUserCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("user@example.com", "--tenant-id", "tenant-123");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-user");
  }

  @Test
  @DisplayName("parse - with all options - should parse correctly")
  void parse_withAllOptions_shouldParseCorrectly() {
    // Arrange
    CreateUserCommand command = new CreateUserCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("user@example.com", "--email", "user@example.com", "--tenant-id", "tenant-123");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-user");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    // Arrange
    CreateUserCommand command = new CreateUserCommand();
    CommandLine cmd = new CommandLine(command);

    // Act & Assert
    assertThat(cmd.getCommandName()).isEqualTo("create-user");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Create a new Descope user");
  }
}
