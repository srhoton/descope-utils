package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for CreateFgaRelationCommand.
 *
 * <p>Tests command-line parsing and option handling for the create-fga-relation command.
 */
class CreateFgaRelationCommandTest {

  @Test
  @DisplayName("parse - with all individual options - should parse correctly")
  void parse_withAllIndividualOptions_shouldParseCorrectly() {
    // Arrange
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
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
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-fga-relation");
  }

  @Test
  @DisplayName("parse - with file option - should parse correctly")
  void parse_withFileOption_shouldParseCorrectly() {
    // Arrange
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("--file", "relations.json");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-fga-relation");
  }

  @Test
  @DisplayName("parse - with short option names - should parse correctly")
  void parse_withShortOptionNames_shouldParseCorrectly() {
    // Arrange
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
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
    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-fga-relation");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    // Arrange
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    // Act & Assert
    assertThat(cmd.getCommandName()).isEqualTo("create-fga-relation");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Create FGA relation tuple(s) between targets and resources");
  }
}
