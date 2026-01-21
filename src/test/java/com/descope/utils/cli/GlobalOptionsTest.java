package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.descope.utils.model.OutputFormat;

import picocli.CommandLine;

/**
 * Unit tests for GlobalOptions.
 *
 * <p>Tests that command-line options are properly parsed and accessible.
 */
class GlobalOptionsTest {

  @Test
  @DisplayName("parse - all options specified - should parse correctly")
  void parse_allOptionsSpecified_shouldParseCorrectly() {
    // Arrange
    GlobalOptions options = new GlobalOptions();
    CommandLine cmd = new CommandLine(options);

    // Act
    cmd.parseArgs(
        "--project-id", "test-project", "--management-key", "test-key", "--output", "JSON");

    // Assert
    assertThat(options.getProjectId()).isEqualTo("test-project");
    assertThat(options.getManagementKey()).isEqualTo("test-key");
    assertThat(options.getOutputFormat()).isEqualTo(OutputFormat.JSON);
    assertThat(options.isHelpRequested()).isFalse();
  }

  @Test
  @DisplayName("parse - short options - should parse correctly")
  void parse_shortOptions_shouldParseCorrectly() {
    // Arrange
    GlobalOptions options = new GlobalOptions();
    CommandLine cmd = new CommandLine(options);

    // Act
    cmd.parseArgs("-p", "test-project", "-k", "test-key", "-o", "TEXT");

    // Assert
    assertThat(options.getProjectId()).isEqualTo("test-project");
    assertThat(options.getManagementKey()).isEqualTo("test-key");
    assertThat(options.getOutputFormat()).isEqualTo(OutputFormat.TEXT);
  }

  @Test
  @DisplayName("parse - no options - should use defaults")
  void parse_noOptions_shouldUseDefaults() {
    // Arrange
    GlobalOptions options = new GlobalOptions();
    CommandLine cmd = new CommandLine(options);

    // Act
    cmd.parseArgs();

    // Assert
    assertThat(options.getProjectId()).isNull();
    assertThat(options.getManagementKey()).isNull();
    assertThat(options.getOutputFormat()).isEqualTo(OutputFormat.TEXT);
    assertThat(options.isHelpRequested()).isFalse();
  }

  @Test
  @DisplayName("parse - help option - should set help requested")
  void parse_helpOption_shouldSetHelpRequested() {
    // Arrange
    GlobalOptions options = new GlobalOptions();
    CommandLine cmd = new CommandLine(options);

    // Act
    cmd.parseArgs("--help");

    // Assert
    assertThat(options.isHelpRequested()).isTrue();
  }
}
