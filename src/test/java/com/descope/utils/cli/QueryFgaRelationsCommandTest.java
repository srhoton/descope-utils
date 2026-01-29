package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for QueryFgaRelationsCommand.
 *
 * <p>Tests command-line parsing and option handling for the query-fga-relations command.
 */
class QueryFgaRelationsCommandTest {

  @Test
  @DisplayName("parse - with who-can-access mode - should parse correctly")
  void parse_withWhoCanAccessMode_shouldParseCorrectly() {
    // Arrange
    QueryFgaRelationsCommand command = new QueryFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs(
        "--mode",
        "who-can-access",
        "--resource",
        "document:report-123",
        "--relation",
        "owner",
        "--namespace",
        "documents");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("query-fga-relations");
  }

  @Test
  @DisplayName("parse - with resource-relations mode - should parse correctly")
  void parse_withResourceRelationsMode_shouldParseCorrectly() {
    // Arrange
    QueryFgaRelationsCommand command = new QueryFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("--mode", "resource-relations", "--resource", "document:report-123");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("query-fga-relations");
  }

  @Test
  @DisplayName("parse - with target-access mode - should parse correctly")
  void parse_withTargetAccessMode_shouldParseCorrectly() {
    // Arrange
    QueryFgaRelationsCommand command = new QueryFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("--mode", "target-access", "--target", "user:alice@example.com");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("query-fga-relations");
  }

  @Test
  @DisplayName("parse - with short option names - should parse correctly")
  void parse_withShortOptionNames_shouldParseCorrectly() {
    // Arrange
    QueryFgaRelationsCommand command = new QueryFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs(
        "-m",
        "who-can-access",
        "-r",
        "document:report-123",
        "--relation",
        "owner",
        "-n",
        "documents");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("query-fga-relations");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    // Arrange
    QueryFgaRelationsCommand command = new QueryFgaRelationsCommand();
    CommandLine cmd = new CommandLine(command);

    // Act & Assert
    assertThat(cmd.getCommandName()).isEqualTo("query-fga-relations");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Query FGA relations with different modes");
  }
}
