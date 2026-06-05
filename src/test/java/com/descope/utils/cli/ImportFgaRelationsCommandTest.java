package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;

/**
 * Unit tests for ImportFgaRelationsCommand.
 *
 * <p>Tests command-line parsing and option handling for the import-fga-relations command.
 */
class ImportFgaRelationsCommandTest {

  @Test
  @DisplayName("parse - with required file option - should parse correctly")
  void parse_withRequiredFileOption_shouldParseCorrectly() {
    ImportFgaRelationsCommand command = new ImportFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("--file", "relations.ndjson");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("import-fga-relations");
  }

  @Test
  @DisplayName("parse - with short file and custom batch size - should parse correctly")
  void parse_withShortFileAndCustomBatchSize_shouldParseCorrectly() {
    ImportFgaRelationsCommand command = new ImportFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    cmd.parseArgs("-f", "relations.ndjson", "-b", "100");

    assertThat(cmd.getCommandSpec().name()).isEqualTo("import-fga-relations");
  }

  @Test
  @DisplayName("parse - without required file option - should throw MissingParameterException")
  void parse_withoutRequiredFileOption_shouldThrowException() {
    ImportFgaRelationsCommand command = new ImportFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    org.assertj.core.api.Assertions.assertThatThrownBy(() -> cmd.parseArgs())
        .isInstanceOf(MissingParameterException.class);
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    ImportFgaRelationsCommand command = new ImportFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    assertThat(cmd.getCommandName()).isEqualTo("import-fga-relations");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Bulk import FGA relation tuples from an NDJSON file");
  }
}
