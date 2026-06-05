package com.descope.utils.model.fga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Batch of FGA resource details for the save-fga-resource-details command.
 *
 * <p>Expected JSON format:
 *
 * <pre>{@code
 * {
 *   "details": [
 *     {"resourceId": "doc1", "resourceType": "document", "displayName": "My Document"}
 *   ]
 * }
 * }</pre>
 */
public class FgaResourceDetailsBatchModel {

  private final List<FgaResourceDetailsModel> details;

  /**
   * Creates a new FgaResourceDetailsBatchModel.
   *
   * @param details The list of resource details (cannot be null or empty)
   */
  @JsonCreator
  public FgaResourceDetailsBatchModel(
      @JsonProperty("details") List<FgaResourceDetailsModel> details) {
    Objects.requireNonNull(details, "Details list cannot be null");
    if (details.isEmpty()) {
      throw new IllegalArgumentException("Details list cannot be empty");
    }
    this.details = new ArrayList<>(details);
  }

  /**
   * Gets the list of resource details.
   *
   * @return A defensive copy of the details list
   */
  public List<FgaResourceDetailsModel> getDetails() {
    return new ArrayList<>(details);
  }

  /**
   * Gets the count of entries in this batch.
   *
   * @return The number of resource details
   */
  public int getCount() {
    return details.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaResourceDetailsBatchModel that = (FgaResourceDetailsBatchModel) o;
    return Objects.equals(details, that.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(details);
  }

  @Override
  public String toString() {
    return "FgaResourceDetailsBatchModel{details=" + details + '}';
  }
}
