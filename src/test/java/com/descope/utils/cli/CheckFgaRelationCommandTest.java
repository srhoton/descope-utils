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
    CheckFgaRelationCommand command = new CheckFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs(
        "--resource", "doc1",
        "--resource-type", "document",
        "--relation", "owner",
        "--target", "user1",
        "--target-type", "user");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("check-fga-relation");
  }

  @Test
  @DisplayName("parse - with short option names - should parse correctly")
  void parse_withShortOptionNames_shouldParseCorrectly() {
    CheckFgaRelationCommand command = new CheckFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs(
        "-r", "doc1",
        "--resource-type", "document",
        "--relation", "owner",
        "-t", "user1",
        "--target-type", "user");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("check-fga-relation");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    CheckFgaRelationCommand command = new CheckFgaRelationCommand();
    CommandLine cmd = new CommandLine(command);

    assertThat(cmd.getCommandName()).isEqualTo("check-fga-relation");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Check if an FGA relation is satisfied between a target and a resource");
  }
}
