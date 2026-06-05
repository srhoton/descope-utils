package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for SaveFgaSchemaCommand.
 *
 * <p>Tests command-line parsing and option handling for the save-fga-schema command.
 */
class SaveFgaSchemaCommandTest {

  @Test
  @DisplayName("parse - with --dsl option - should parse correctly")
  void parse_withDslOption_shouldParseCorrectly() {
    SaveFgaSchemaCommand command = new SaveFgaSchemaCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--dsl", "model AuthZ 1.0\ntype user");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("save-fga-schema");
  }

  @Test
  @DisplayName("parse - with --file option - should parse correctly")
  void parse_withFileOption_shouldParseCorrectly() {
    SaveFgaSchemaCommand command = new SaveFgaSchemaCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--file", "schema.authz");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("save-fga-schema");
  }

  @Test
  @DisplayName("parse - with short file option - should parse correctly")
  void parse_withShortFileOption_shouldParseCorrectly() {
    SaveFgaSchemaCommand command = new SaveFgaSchemaCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("-f", "schema.authz");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("save-fga-schema");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    SaveFgaSchemaCommand command = new SaveFgaSchemaCommand();
    CommandLine cmd = new CommandLine(command);

    assertThat(cmd.getCommandName()).isEqualTo("save-fga-schema");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Save (create or update) an FGA schema in AuthZ 1.0 DSL format");
  }
}
