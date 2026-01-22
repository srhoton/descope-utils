package com.descope.utils.output;

import com.descope.utils.model.OperationResult;
import com.descope.utils.model.OutputFormat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for formatting operation results in different output formats.
 *
 * <p>Supports both JSON and human-readable text formats.
 */
@ApplicationScoped
public class OutputFormatter {

  private final JsonFormatter jsonFormatter;
  private final TextFormatter textFormatter;

  @Inject
  public OutputFormatter(JsonFormatter jsonFormatter, TextFormatter textFormatter) {
    this.jsonFormatter = jsonFormatter;
    this.textFormatter = textFormatter;
  }

  /**
   * Formats an operation result according to the specified output format.
   *
   * @param result The operation result to format
   * @param format The desired output format
   * @param <T> The type of data in the result
   * @return The formatted output string
   */
  public <T> String format(OperationResult<T> result, OutputFormat format) {
    return switch (format) {
      case JSON -> jsonFormatter.format(result);
      case TEXT -> textFormatter.format(result);
    };
  }
}
