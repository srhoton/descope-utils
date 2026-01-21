package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for CreateAppCommand.
 *
 * <p>Tests command-line parsing and option handling for the create-app command.
 */
class CreateAppCommandTest {

  @Test
  @DisplayName("parse - with name only - should parse correctly")
  void parse_withNameOnly_shouldParseCorrectly() {
    // Arrange
    CreateAppCommand command = new CreateAppCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("MyApp");

    // Assert - command parsing succeeded
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-app");
  }

  @Test
  @DisplayName("parse - with name and description - should parse correctly")
  void parse_withNameAndDescription_shouldParseCorrectly() {
    // Arrange
    CreateAppCommand command = new CreateAppCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("MyApp", "--description", "My application description");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-app");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    // Arrange
    CreateAppCommand command = new CreateAppCommand();
    CommandLine cmd = new CommandLine(command);

    // Act & Assert
    assertThat(cmd.getCommandName()).isEqualTo("create-app");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Create a new Descope application");
  }
}
