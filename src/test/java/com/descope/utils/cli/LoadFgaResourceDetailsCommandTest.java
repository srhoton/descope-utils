package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for LoadFgaResourceDetailsCommand.
 *
 * <p>Tests command-line parsing and option handling for the load-fga-resource-details command.
 */
class LoadFgaResourceDetailsCommandTest {

  @Test
  @DisplayName("parse - with individual options - should parse correctly")
  void parse_withIndividualOptions_shouldParseCorrectly() {
    LoadFgaResourceDetailsCommand command = new LoadFgaResourceDetailsCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--resource-id", "doc1", "--resource-type", "document");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("load-fga-resource-details");
  }

  @Test
  @DisplayName("parse - with file option - should parse correctly")
  void parse_withFileOption_shouldParseCorrectly() {
    LoadFgaResourceDetailsCommand command = new LoadFgaResourceDetailsCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--file", "identifiers.json");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("load-fga-resource-details");
  }

  @Test
  @DisplayName("parse - with short file option - should parse correctly")
  void parse_withShortFileOption_shouldParseCorrectly() {
    LoadFgaResourceDetailsCommand command = new LoadFgaResourceDetailsCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("-f", "identifiers.json");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("load-fga-resource-details");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    LoadFgaResourceDetailsCommand command = new LoadFgaResourceDetailsCommand();
    CommandLine cmd = new CommandLine(command);

    assertThat(cmd.getCommandName()).isEqualTo("load-fga-resource-details");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Load display metadata (display name) for FGA resources");
  }
}
