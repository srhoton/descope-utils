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
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs(
        "--resource", "doc1",
        "--resource-type", "document",
        "--relation", "owner",
        "--target", "user1",
        "--target-type", "user");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-fga-relation");
  }

  @Test
  @DisplayName("parse - with file option - should parse correctly")
  void parse_withFileOption_shouldParseCorrectly() {
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--file", "relations.json");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-fga-relation");
  }

  @Test
  @DisplayName("parse - with short option names - should parse correctly")
  void parse_withShortOptionNames_shouldParseCorrectly() {
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs(
        "-r", "doc1",
        "--resource-type", "document",
        "--relation", "owner",
        "-t", "user1",
        "--target-type", "user");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-fga-relation");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    assertThat(cmd.getCommandName()).isEqualTo("create-fga-relation");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Create FGA relation tuple(s) between targets and resources");
  }

  @Test
  @DisplayName("parse - with file short option - should parse correctly")
  void parse_withFileShortOption_shouldParseCorrectly() {
    CreateFgaRelationCommand command = new CreateFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("-f", "relations.json");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("create-fga-relation");
  }
}
