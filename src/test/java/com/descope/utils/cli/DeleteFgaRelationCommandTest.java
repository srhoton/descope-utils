package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for DeleteFgaRelationCommand.
 *
 * <p>Tests command-line parsing and option handling for the delete-fga-relation command.
 */
class DeleteFgaRelationCommandTest {

  @Test
  @DisplayName("parse - with all individual options - should parse correctly")
  void parse_withAllIndividualOptions_shouldParseCorrectly() {
    DeleteFgaRelationCommand command = new DeleteFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs(
        "--resource", "doc1",
        "--resource-type", "document",
        "--relation", "owner",
        "--target", "user1",
        "--target-type", "user");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("delete-fga-relation");
  }

  @Test
  @DisplayName("parse - with file option - should parse correctly")
  void parse_withFileOption_shouldParseCorrectly() {
    DeleteFgaRelationCommand command = new DeleteFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--file", "relations.json");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("delete-fga-relation");
  }

  @Test
  @DisplayName("parse - with short option names - should parse correctly")
  void parse_withShortOptionNames_shouldParseCorrectly() {
    DeleteFgaRelationCommand command = new DeleteFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs(
        "-r", "doc1",
        "--resource-type", "document",
        "--relation", "owner",
        "-t", "user1",
        "--target-type", "user");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("delete-fga-relation");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    DeleteFgaRelationCommand command = new DeleteFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    assertThat(cmd.getCommandName()).isEqualTo("delete-fga-relation");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Delete FGA relation tuple(s) between targets and resources");
  }
}
