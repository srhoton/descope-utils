package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for DescopeUtilsCommand.
 *
 * <p>Tests the main command behavior and help output.
 */
class DescopeUtilsCommandTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  @DisplayName("run - no subcommand - should print help message")
  void run_noSubcommand_shouldPrintHelpMessage() {
    // Arrange
    DescopeUtilsCommand command = new DescopeUtilsCommand();

    // Act
    command.run();

    // Assert
    String output = outContent.toString();
    assertThat(output).contains("Please specify a subcommand");
    assertThat(output).contains("--help");
  }

  @Test
  @DisplayName("parse - with help option - should have subcommands")
  void parse_withHelpOption_shouldHaveSubcommands() {
    // Arrange
    CommandLine cmd = new CommandLine(new DescopeUtilsCommand());

    // Act & Assert
    assertThat(cmd.getSubcommands()).containsKeys("create-app", "create-tenant", "create-user");
  }
}
