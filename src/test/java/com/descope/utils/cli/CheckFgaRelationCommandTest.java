package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for CheckFgaRelationCommand.
 *
 * <p>Tests command-line parsing and option handling for the check-fga-relation command.
 */
class CheckFgaRelationCommandTest {

  @Test
  @DisplayName("parse - with all required options - should parse correctly")
  void parse_withAllRequiredOptions_shouldParseCorrectly() {
    // Arrange
    CheckFgaRelationCommand command = new CheckFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs(
        "--resource",
        "document:report-123",
        "--relation",
        "owner",
        "--namespace",
        "documents",
        "--target",
        "user:alice@example.com");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("check-fga-relation");
  }

  @Test
  @DisplayName("parse - with short option names - should parse correctly")
  void parse_withShortOptionNames_shouldParseCorrectly() {
    // Arrange
    CheckFgaRelationCommand command = new CheckFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs(
        "-r",
        "document:report-123",
        "--relation",
        "owner",
        "-n",
        "documents",
        "-t",
        "user:alice@example.com");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("check-fga-relation");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    // Arrange
    CheckFgaRelationCommand command = new CheckFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    // Act & Assert
    assertThat(cmd.getCommandName()).isEqualTo("check-fga-relation");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Check if FGA relation tuple(s) exist");
  }
}
