package com.descope.utils.model.fga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a batch of FGA relation tuples for bulk operations.
 *
 * <p>This model is used when loading relation tuples from JSON files for batch create/delete
 * operations.
 */
public class RelationBatchModel {

  private final List<RelationTupleModel> relations;

  /**
   * Creates a new RelationBatchModel instance.
   *
   * @param relations The list of relation tuples (cannot be null or empty)
   * @throws NullPointerException if relations is null
   * @throws IllegalArgumentException if relations is empty
   */
  @JsonCreator
  public RelationBatchModel(@JsonProperty("relations") List<RelationTupleModel> relations) {
    Objects.requireNonNull(relations, "Relations list cannot be null");
    if (relations.isEmpty()) {
      throw new IllegalArgumentException("Relations list cannot be empty");
    }
    this.relations = new ArrayList<>(relations);
  }

  /**
   * Gets the list of relation tuples.
   *
   * @return A defensive copy of the relations list
   */
  public List<RelationTupleModel> getRelations() {
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
    RelationBatchModel that = (RelationBatchModel) o;
    return Objects.equals(relations, that.relations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relations);
  }

  @Override
  public String toString() {
    return "RelationBatchModel{" + "relations=" + relations + ", count=" + getCount() + '}';
  }
}
