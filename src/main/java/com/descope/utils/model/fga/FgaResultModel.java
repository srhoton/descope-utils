package com.descope.utils.model.fga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the result of an FGA query operation.
 *
 * <p>This model is used to return results from FGA operations such as checking relations, querying
 * who can access a resource, or querying what a subject can access.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FgaResultModel {

  private final int count;
  private final List<RelationTupleModel> relations;
  private final Boolean exists;

  /**
   * Creates a new FgaResultModel for query results.
   *
   * @param relations The list of relation tuples found
   */
  public FgaResultModel(List<RelationTupleModel> relations) {
    this.relations = relations != null ? new ArrayList<>(relations) : new ArrayList<>();
    this.count = this.relations.size();
    this.exists = null;
  }

  /**
   * Creates a new FgaResultModel for check operations.
   *
   * @param exists Whether the queried relation exists
   * @param relation The relation that was checked (optional)
   */
  public FgaResultModel(boolean exists, RelationTupleModel relation) {
    this.exists = exists;
    this.relations = relation != null ? List.of(relation) : new ArrayList<>();
    this.count = this.relations.size();
  }

  /**
   * Creates a new FgaResultModel instance (for JSON deserialization).
   *
   * @param count The count of results
   * @param relations The list of relation tuples
   * @param exists Whether a relation exists (for check operations)
   */
  @JsonCreator
  public FgaResultModel(
      @JsonProperty("count") int count,
      @JsonProperty("relations") List<RelationTupleModel> relations,
      @JsonProperty("exists") Boolean exists) {
    this.count = count;
    this.relations = relations != null ? new ArrayList<>(relations) : new ArrayList<>();
    this.exists = exists;
  }

  /**
   * Gets the count of results.
   *
   * @return The number of relations found
   */
  public int getCount() {
    return count;
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
   * Gets whether the queried relation exists.
   *
   * @return True if exists, false if not, null if not a check operation
   */
  public Boolean getExists() {
    return exists;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaResultModel that = (FgaResultModel) o;
    return count == that.count
        && Objects.equals(relations, that.relations)
        && Objects.equals(exists, that.exists);
  }

  @Override
  public int hashCode() {
    return Objects.hash(count, relations, exists);
  }

  @Override
  public String toString() {
    return "FgaResultModel{"
        + "count="
        + count
        + ", relations="
        + relations
        + ", exists="
        + exists
        + '}';
  }
}
