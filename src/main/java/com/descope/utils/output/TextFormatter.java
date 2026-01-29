package com.descope.utils.output;

import com.descope.utils.model.Application;
import com.descope.utils.model.FederatedApplication;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.Tenant;
import com.descope.utils.model.User;
import com.descope.utils.model.rebac.NamespaceModel;
import com.descope.utils.model.rebac.RelationDefinitionModel;
import com.descope.utils.model.rebac.SchemaModel;

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

    if (result.getData() != null) {
      sb.append(SEPARATOR).append("\n");
      sb.append(formatData(result.getData()));
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
    } else if (data instanceof FederatedApplication fedApp) {
      return formatFederatedApplication(fedApp);
    } else if (data instanceof Tenant tenant) {
      return formatTenant(tenant);
    } else if (data instanceof User user) {
      return formatUser(user);
    } else if (data instanceof SchemaModel schema) {
      return formatSchema(schema);
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
   * Formats a FederatedApplication for display.
   *
   * @param fedApp The federated application to format
   * @return Formatted federated application string
   */
  private String formatFederatedApplication(FederatedApplication fedApp) {
    StringBuilder sb = new StringBuilder();
    sb.append("Federated Application Details:\n");
    sb.append("  ID:          ").append(fedApp.getId()).append("\n");
    sb.append("  Name:        ").append(fedApp.getName()).append("\n");
    sb.append("  Type:        ").append(fedApp.getType()).append("\n");
    if (fedApp.getDescription() != null && !fedApp.getDescription().isEmpty()) {
      sb.append("  Description: ").append(fedApp.getDescription()).append("\n");
    }
    if (fedApp.getLoginPageUrl() != null && !fedApp.getLoginPageUrl().isEmpty()) {
      sb.append("  Login URL:   ").append(fedApp.getLoginPageUrl()).append("\n");
    }
    if (fedApp.getCreatedAt() != null) {
      sb.append("  Created:     ").append(fedApp.getCreatedAt()).append("\n");
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

  /**
   * Formats a ReBAC Schema for display.
   *
   * @param schema The schema to format
   * @return Formatted schema string
   */
  private String formatSchema(SchemaModel schema) {
    StringBuilder sb = new StringBuilder();
    sb.append("ReBAC Schema:\n");

    if (schema.getName() != null) {
      sb.append("  Name: ").append(schema.getName()).append("\n");
    }

    sb.append("  Namespaces: ").append(schema.getNamespaces().size()).append("\n\n");

    for (NamespaceModel namespace : schema.getNamespaces()) {
      sb.append("  Namespace: ").append(namespace.getName()).append("\n");
      sb.append("    Relations:\n");

      if (namespace.getRelationDefinitions().isEmpty()) {
        sb.append("      (none)\n");
      } else {
        for (RelationDefinitionModel relation : namespace.getRelationDefinitions()) {
          sb.append("      - ").append(relation.getName());

          if (!relation.getTargetNamespaces().isEmpty()) {
            sb.append(" → [").append(String.join(", ", relation.getTargetNamespaces())).append("]");
          }

          sb.append("\n");
        }
      }

      sb.append("\n");
    }

    return sb.toString();
  }
}
