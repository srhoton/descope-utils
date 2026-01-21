package com.descope.utils.output;

import com.descope.utils.model.Application;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.Tenant;
import com.descope.utils.model.User;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Formatter for human-readable text output.
 *
 * <p>Converts operation results to formatted text strings suitable for console display.
 */
@ApplicationScoped
public class TextFormatter {

  private static final String SUCCESS_PREFIX = "✓ ";
  private static final String ERROR_PREFIX = "✗ ";
  private static final String SEPARATOR = "─".repeat(60);

  /**
   * Formats an operation result as human-readable text.
   *
   * @param result The operation result to format
   * @param <T> The type of data in the result
   * @return Text string representation
   */
  public <T> String format(OperationResult<T> result) {
    if (result.isSuccess()) {
      return formatSuccess(result);
    } else {
      return formatError(result);
    }
  }

  /**
   * Formats a successful operation result.
   *
   * @param result The successful result
   * @param <T> The type of data
   * @return Formatted success message
   */
  private <T> String formatSuccess(OperationResult<T> result) {
    StringBuilder sb = new StringBuilder();
    sb.append(SUCCESS_PREFIX).append(result.getMessage()).append("\n");

    if (result.getData().isPresent()) {
      sb.append(SEPARATOR).append("\n");
      sb.append(formatData(result.getData().get()));
    }

    return sb.toString();
  }

  /**
   * Formats a failed operation result.
   *
   * @param result The failed result
   * @param <T> The type of data
   * @return Formatted error message
   */
  private <T> String formatError(OperationResult<T> result) {
    return ERROR_PREFIX + "Error: " + result.getErrorMessage() + "\n";
  }

  /**
   * Formats specific data types into human-readable format.
   *
   * @param data The data to format
   * @return Formatted data string
   */
  private String formatData(Object data) {
    if (data instanceof Application app) {
      return formatApplication(app);
    } else if (data instanceof Tenant tenant) {
      return formatTenant(tenant);
    } else if (data instanceof User user) {
      return formatUser(user);
    } else {
      return data.toString();
    }
  }

  /**
   * Formats an Application for display.
   *
   * @param app The application to format
   * @return Formatted application string
   */
  private String formatApplication(Application app) {
    StringBuilder sb = new StringBuilder();
    sb.append("Application Details:\n");
    sb.append("  ID:          ").append(app.getId()).append("\n");
    sb.append("  Name:        ").append(app.getName()).append("\n");
    if (app.getDescription() != null) {
      sb.append("  Description: ").append(app.getDescription()).append("\n");
    }
    if (app.getCreatedAt() != null) {
      sb.append("  Created:     ").append(app.getCreatedAt()).append("\n");
    }
    return sb.toString();
  }

  /**
   * Formats a Tenant for display.
   *
   * @param tenant The tenant to format
   * @return Formatted tenant string
   */
  private String formatTenant(Tenant tenant) {
    StringBuilder sb = new StringBuilder();
    sb.append("Tenant Details:\n");
    sb.append("  ID:          ").append(tenant.getId()).append("\n");
    sb.append("  Name:        ").append(tenant.getName()).append("\n");
    sb.append("  App ID:      ").append(tenant.getAppId()).append("\n");
    if (tenant.getCreatedAt() != null) {
      sb.append("  Created:     ").append(tenant.getCreatedAt()).append("\n");
    }
    return sb.toString();
  }

  /**
   * Formats a User for display.
   *
   * @param user The user to format
   * @return Formatted user string
   */
  private String formatUser(User user) {
    StringBuilder sb = new StringBuilder();
    sb.append("User Details:\n");
    sb.append("  ID:          ").append(user.getId()).append("\n");
    sb.append("  Login ID:    ").append(user.getLoginId()).append("\n");
    if (user.getEmail() != null) {
      sb.append("  Email:       ").append(user.getEmail()).append("\n");
    }
    sb.append("  Tenant ID:   ").append(user.getTenantId()).append("\n");
    if (user.getCreatedAt() != null) {
      sb.append("  Created:     ").append(user.getCreatedAt()).append("\n");
    }
    return sb.toString();
  }
}
