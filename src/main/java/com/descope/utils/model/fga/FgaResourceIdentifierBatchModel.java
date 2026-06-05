package com.descope.utils.model.fga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Batch of FGA resource identifiers for the load-fga-resource-details command.
 *
 * <p>Expected JSON format:
 *
 * <pre>{@code
 * {
 *   "identifiers": [
 *     {"resourceId": "doc1", "resourceType": "document"}
 *   ]
 * }
 * }</pre>
 */
public class FgaResourceIdentifierBatchModel {

  private final List<FgaResourceIdentifierModel> identifiers;

  /**
   * Creates a new FgaResourceIdentifierBatchModel.
   *
   * @param identifiers The list of resource identifiers (cannot be null or empty)
   */
  @JsonCreator
  public FgaResourceIdentifierBatchModel(
      @JsonProperty("identifiers") List<FgaResourceIdentifierModel> identifiers) {
    Objects.requireNonNull(identifiers, "Identifiers list cannot be null");
    if (identifiers.isEmpty()) {
      throw new IllegalArgumentException("Identifiers list cannot be empty");
    }
    this.identifiers = new ArrayList<>(identifiers);
  }

  /**
   * Gets the list of resource identifiers.
   *
   * @return A defensive copy of the identifiers list
   */
  public List<FgaResourceIdentifierModel> getIdentifiers() {
    return new ArrayList<>(identifiers);
  }

  /**
   * Gets the count of identifiers in this batch.
   *
   * @return The number of identifiers
   */
  public int getCount() {
    return identifiers.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaResourceIdentifierBatchModel that = (FgaResourceIdentifierBatchModel) o;
    return Objects.equals(identifiers, that.identifiers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifiers);
  }

  @Override
  public String toString() {
    return "FgaResourceIdentifierBatchModel{identifiers=" + identifiers + '}';
  }
}
