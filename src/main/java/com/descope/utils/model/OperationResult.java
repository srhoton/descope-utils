package com.descope.utils.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents the result of a CLI operation.
 *
 * @param <T> The type of data returned by the operation
 */
public class OperationResult<T> {

  private final boolean success;
  private final boolean created;
  private final boolean alreadyExists;
  private final T data;
  private final String message;
  private final String errorMessage;

  /**
   * Creates a successful operation result.
   *
   * @param data The operation data
   * @param message A success message
   * @param created Whether the resource was newly created
   * @param alreadyExists Whether the resource already existed
   */
  private OperationResult(T data, String message, boolean created, boolean alreadyExists) {
    this.success = true;
    this.created = created;
    this.alreadyExists = alreadyExists;
    this.data = data;
    this.message = message;
    this.errorMessage = null;
  }

  /**
   * Creates a failed operation result.
   *
   * @param errorMessage The error message
   */
  private OperationResult(String errorMessage) {
    this.success = false;
    this.created = false;
    this.alreadyExists = false;
    this.data = null;
    this.message = null;
    this.errorMessage = Objects.requireNonNull(errorMessage, "Error message cannot be null");
  }

  /**
   * Creates a successful operation result.
   *
   * @param data The operation data
   * @param message A success message
   * @param <T> The type of data
   * @return A successful OperationResult
   */
  public static <T> OperationResult<T> success(T data, String message) {
    return new OperationResult<>(data, message, false, false);
  }

  /**
   * Creates a successful operation result with default message.
   *
   * @param data The operation data
   * @param <T> The type of data
   * @return A successful OperationResult
   */
  public static <T> OperationResult<T> success(T data) {
    return new OperationResult<>(data, "Operation completed successfully", false, false);
  }

  /**
   * Creates a successful result for a newly created resource.
   *
   * @param data The created resource data
   * @param message A success message
   * @param <T> The type of data
   * @return A successful OperationResult marked as created
   */
  public static <T> OperationResult<T> created(T data, String message) {
    return new OperationResult<>(data, message, true, false);
  }

  /**
   * Creates a successful result for a resource that already exists.
   *
   * @param data The existing resource data
   * @param message A success message
   * @param <T> The type of data
   * @return A successful OperationResult marked as already exists
   */
  public static <T> OperationResult<T> alreadyExists(T data, String message) {
    return new OperationResult<>(data, message, false, true);
  }

  /**
   * Creates a failed operation result.
   *
   * @param errorMessage The error message
   * @param <T> The type of data
   * @return A failed OperationResult
   */
  public static <T> OperationResult<T> failure(String errorMessage) {
    return new OperationResult<>(errorMessage);
  }

  /**
   * Checks if the operation was successful.
   *
   * @return true if the operation succeeded, false otherwise
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Checks if the resource was newly created.
   *
   * @return true if the resource was created, false otherwise
   */
  public boolean isCreated() {
    return created;
  }

  /**
   * Checks if the resource already existed.
   *
   * @return true if the resource already existed, false otherwise
   */
  public boolean isAlreadyExists() {
    return alreadyExists;
  }

  /**
   * Gets the operation data.
   *
   * @return The data object, or null if the operation failed
   */
  public T getData() {
    return data;
  }

  /**
   * Gets the operation data as an Optional.
   *
   * @return An Optional containing the data if the operation was successful, empty otherwise
   */
  public Optional<T> getDataOptional() {
    return Optional.ofNullable(data);
  }

  /**
   * Gets the success message.
   *
   * @return The success message, or null if the operation failed
   */
  public String getMessage() {
    return message;
  }

  /**
   * Gets the error message.
   *
   * @return The error message, or null if the operation succeeded
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OperationResult<?> that = (OperationResult<?>) o;
    return success == that.success
        && created == that.created
        && alreadyExists == that.alreadyExists
        && Objects.equals(data, that.data)
        && Objects.equals(message, that.message)
        && Objects.equals(errorMessage, that.errorMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(success, created, alreadyExists, data, message, errorMessage);
  }

  @Override
  public String toString() {
    if (success) {
      String status = created ? "created" : (alreadyExists ? "already exists" : "success");
      return "OperationResult{"
          + "status='"
          + status
          + "', message='"
          + message
          + "', data="
          + data
          + "}";
    } else {
      return "OperationResult{success=false, errorMessage='" + errorMessage + "'}";
    }
  }
}
