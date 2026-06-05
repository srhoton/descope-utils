package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for LoadFgaSchemaCommand.
 *
 * <p>Tests command-line parsing and option handling for the load-fga-schema command.
 */
class LoadFgaSchemaCommandTest {

  @Test
  @DisplayName("parse - with no options - should parse correctly")
  void parse_withNoOptions_shouldParseCorrectly() {
    LoadFgaSchemaCommand command = new LoadFgaSchemaCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs();

    assertThat(cmd.getCommandSpec().name()).isEqualTo("load-fga-schema");
  }

  @Test
  @DisplayName("parse - with project-id and management-key - should parse correctly")
  void parse_withProjectIdAndManagementKey_shouldParseCorrectly() {
    LoadFgaSchemaCommand command = new LoadFgaSchemaCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--project-id", "Ptest123", "--management-key", "key456");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("load-fga-schema");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    LoadFgaSchemaCommand command = new LoadFgaSchemaCommand();
    CommandLine cmd = new CommandLine(command);

    assertThat(cmd.getCommandName()).isEqualTo("load-fga-schema");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Load the current FGA schema (AuthZ 1.0 DSL format)");
  }
}
