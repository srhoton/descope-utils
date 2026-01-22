package com.descope.utils.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Unit tests for AddAppToTenantCommand.
 *
 * <p>Tests command-line parsing and option handling for the add-app-to-tenant command.
 */
class AddAppToTenantCommandTest {

  @Test
  @DisplayName("parse - with tenant-id and app-id - should parse correctly")
  void parse_withTenantIdAndAppId_shouldParseCorrectly() {
    // Arrange
    AddAppToTenantCommand command = new AddAppToTenantCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("--tenant-id", "test-tenant", "--app-id", "app-123");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("add-app-to-tenant");
  }

  @Test
  @DisplayName("parse - with short options - should parse correctly")
  void parse_withShortOptions_shouldParseCorrectly() {
    // Arrange
    AddAppToTenantCommand command = new AddAppToTenantCommand();
    CommandLine cmd = new CommandLine(command);

    // Act
    cmd.parseArgs("-t", "test-tenant", "-a", "app-123");

    // Assert
    assertThat(cmd.getCommandSpec().name()).isEqualTo("add-app-to-tenant");
  }

  @Test
  @DisplayName("commandSpec - should have correct name and description")
  void commandSpec_shouldHaveCorrectNameAndDescription() {
    // Arrange
    AddAppToTenantCommand command = new AddAppToTenantCommand();
    CommandLine cmd = new CommandLine(command);

    // Act & Assert
    assertThat(cmd.getCommandName()).isEqualTo("add-app-to-tenant");
    assertThat(cmd.getCommandSpec().usageMessage().description())
        .contains("Associate an application with a tenant");
  }

  @Test
  @DisplayName("command structure - should have correct annotations")
  void commandStructure_shouldHaveCorrectAnnotations() {
    // Verify command is properly annotated
    assertThat(AddAppToTenantCommand.class.isAnnotationPresent(picocli.CommandLine.Command.class))
        .isTrue();

    picocli.CommandLine.Command annotation =
        AddAppToTenantCommand.class.getAnnotation(picocli.CommandLine.Command.class);
    assertThat(annotation.name()).isEqualTo("add-app-to-tenant");
    assertThat(annotation.description()).contains("Associate an application with a tenant");
  }

  @Test
  @DisplayName("command options - should have required tenant-id and app-id options")
  void commandOptions_shouldHaveRequiredOptions() throws NoSuchFieldException {
    // Verify tenant-id option
    java.lang.reflect.Field tenantIdField =
        AddAppToTenantCommand.class.getDeclaredField("tenantId");
    assertThat(tenantIdField.isAnnotationPresent(picocli.CommandLine.Option.class)).isTrue();
    picocli.CommandLine.Option tenantOption =
        tenantIdField.getAnnotation(picocli.CommandLine.Option.class);
    assertThat(tenantOption.names()).contains("-t", "--tenant-id");
    assertThat(tenantOption.required()).isTrue();

    // Verify app-id option
    java.lang.reflect.Field appIdField = AddAppToTenantCommand.class.getDeclaredField("appId");
    assertThat(appIdField.isAnnotationPresent(picocli.CommandLine.Option.class)).isTrue();
    picocli.CommandLine.Option appOption =
        appIdField.getAnnotation(picocli.CommandLine.Option.class);
    assertThat(appOption.names()).contains("-a", "--app-id");
    assertThat(appOption.required()).isTrue();
  }
}
