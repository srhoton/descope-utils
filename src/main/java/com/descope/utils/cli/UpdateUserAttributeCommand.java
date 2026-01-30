package com.descope.utils.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.output.OutputFormatter;
import com.descope.utils.service.UserService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to update a custom attribute on an existing user.
 *
 * <p>The custom attribute key must be pre-configured in the Descope console. The value type must
 * match the declared type for that attribute.
 */
@Command(
    name = "update-user-attribute",
    description = "Update a custom attribute on an existing user",
    mixinStandardHelpOptions = true)
public class UpdateUserAttributeCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(UpdateUserAttributeCommand.class);

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "User login ID (email, phone, or username)")
  private String loginId;

  @Option(
      names = {"-a", "--attribute"},
      description = "Custom attribute key (must be configured in Descope console)",
      required = true)
  private String attributeKey;

  @Option(
      names = {"-v", "--value"},
      description = "Value to set for the attribute",
      required = true)
  private String attributeValue;

  @Option(
      names = {"--type"},
      description =
          "Value type: STRING (default), NUMBER, BOOLEAN. "
              + "Use NUMBER for integers/decimals, BOOLEAN for true/false",
      defaultValue = "STRING")
  private ValueType valueType;

  @Inject private ConfigurationService configService;
  @Inject private UserService userService;
  @Inject private OutputFormatter outputFormatter;

  /** Supported value types for custom attributes. */
  public enum ValueType {
    STRING,
    NUMBER,
    BOOLEAN
  }

  @Override
  public void run() {
    try {
      // Load configuration
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      logger.info(
          "Updating custom attribute '{}' for user: {} with value: {} (type: {})",
          attributeKey,
          loginId,
          attributeValue,
          valueType);

      // Convert value to appropriate type
      Object typedValue = convertValue(attributeValue, valueType);

      // Update the custom attribute
      OperationResult<Void> result =
          userService.updateCustomAttribute(config, loginId, attributeKey, typedValue);

      // Format and print the result
      String output = outputFormatter.format(result, globalOptions.getOutputFormat());
      System.out.println(output);

      // Exit with appropriate code
      System.exit(result.isSuccess() ? 0 : 1);

    } catch (Exception e) {
      logger.error("Failed to update custom attribute", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }

  private Object convertValue(String value, ValueType type) {
    switch (type) {
      case NUMBER:
        try {
          if (value.contains(".")) {
            return Double.parseDouble(value);
          }
          return Long.parseLong(value);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Invalid number format: '" + value + "'. Expected an integer or decimal.");
        }
      case BOOLEAN:
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
          return Boolean.parseBoolean(value);
        }
        throw new IllegalArgumentException(
            "Invalid boolean value: '" + value + "'. Expected 'true' or 'false'.");
      case STRING:
      default:
        return value;
    }
  }
}
