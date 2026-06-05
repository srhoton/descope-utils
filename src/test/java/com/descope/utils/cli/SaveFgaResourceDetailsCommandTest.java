package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for SaveFgaResourceDetailsCommand.
 *
 * <p>Tests command-line parsing and option handling for the save-fga-resource-details command.
 */
class SaveFgaResourceDetailsCommandTest {

  @Test
  @DisplayName("parse - with all individual options - should parse correctly")
  void parse_withAllIndividualOptions_shouldParseCorrectly() {
    SaveFgaResourceDetailsCommand command = new SaveFgaResourceDetailsCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs(
        "--resource-id", "doc1",
        "--resource-type", "document",
        "--display-name", "My Document");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("save-fga-resource-details");
  }

  @Test
  @DisplayName("parse - with file option - should parse correctly")
  void parse_withFileOption_shouldParseCorrectly() {
    SaveFgaResourceDetailsCommand command = new SaveFgaResourceDetailsCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--file", "details.json");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("save-fga-resource-details");
  }

  @Test
  @DisplayName("parse - with short file option - should parse correctly")
  void parse_withShortFileOption_shouldParseCorrectly() {
    SaveFgaResourceDetailsCommand command = new SaveFgaResourceDetailsCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("-f", "details.json");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("save-fga-resource-details");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    SaveFgaResourceDetailsCommand command = new SaveFgaResourceDetailsCommand();
    CommandLine cmd = new CommandLine(command);

    assertThat(cmd.getCommandName()).isEqualTo("save-fga-resource-details");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Save display metadata (display name) for FGA resources");
  }
}
