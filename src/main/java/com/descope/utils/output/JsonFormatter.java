package com.descope.utils.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.model.OperationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Formatter for JSON output.
 *
 * <p>Converts operation results to formatted JSON strings.
 */
@ApplicationScoped
public class JsonFormatter {

  private static final Logger logger = LoggerFactory.getLogger(JsonFormatter.class);
  private final ObjectMapper objectMapper;

  public JsonFormatter() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.registerModule(new Jdk8Module());
    this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    this.objectMapper.setSerializationInclusion(
        com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
  }

  /**
   * Formats an operation result as JSON.
   *
   * @param result The operation result to format
   * @param <T> The type of data in the result
   * @return JSON string representation
   */
  public <T> String format(OperationResult<T> result) {
    try {
      if (result.isSuccess()) {
        JsonResponse<T> response =
            new JsonResponse<>(true, result.getData().orElse(null), result.getMessage(), null);
        return objectMapper.writeValueAsString(response);
      } else {
        JsonResponse<T> response = new JsonResponse<>(false, null, null, result.getErrorMessage());
        return objectMapper.writeValueAsString(response);
      }
    } catch (JsonProcessingException e) {
      logger.error("Failed to serialize result to JSON", e);
      return "{\"success\": false, \"error\": \"Failed to format response as JSON\"}";
    }
  }

  /**
   * Internal record for JSON response structure.
   *
   * @param <T> The type of data
   */
  private record JsonResponse<T>(boolean success, T data, String message, String error) {}
}
