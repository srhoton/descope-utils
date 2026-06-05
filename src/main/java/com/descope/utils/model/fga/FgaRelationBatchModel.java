package com.descope.utils.model.fga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a batch of FGA relations for bulk create/delete operations.
 *
 * <p>This model is used when loading FGA relations from a JSON file. The expected JSON format is:
 *
 * <pre>{@code
 * {
 *   "relations": [
 *     {"resource": "doc1", "resourceType": "document", "relation": "owner",
 *      "target": "user1", "targetType": "user"}
 *   ]
 * }
 * }</pre>
 */
public class FgaRelationBatchModel {

  private final List<FgaRelationModel> relations;

  /**
   * Creates a new FgaRelationBatchModel instance.
   *
   * @param relations The list of FGA relations (cannot be null or empty)
   */
  @JsonCreator
  public FgaRelationBatchModel(@JsonProperty("relations") List<FgaRelationModel> relations) {
    Objects.requireNonNull(relations, "Relations list cannot be null");
    if (relations.isEmpty()) {
      throw new IllegalArgumentException("Relations list cannot be empty");
    }
    this.relations = new ArrayList<>(relations);
  }

  /**
   * Gets the list of FGA relations.
   *
   * @return A defensive copy of the relations list
   */
  public List<FgaRelationModel> getRelations() {
    return new ArrayList<>(relations);
  }

  /**
   * Gets the count of relations in this batch.
   *
   * @return The number of relations
   */
  public int getCount() {
    return relations.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaRelationBatchModel that = (FgaRelationBatchModel) o;
    return Objects.equals(relations, that.relations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relations);
  }

  @Override
  public String toString() {
    return "FgaRelationBatchModel{relations=" + relations + ", count=" + getCount() + '}';
  }
}
