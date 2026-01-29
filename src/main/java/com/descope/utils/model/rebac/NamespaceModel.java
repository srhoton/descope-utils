package com.descope.utils.model.rebac;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a namespace in a ReBAC schema.
 *
 * <p>A namespace groups related resources and defines the relation types that can exist for those
 * resources.
 */
public class NamespaceModel {

  private final String name;
  private final List<RelationDefinitionModel> relationDefinitions;

  /**
   * Creates a new NamespaceModel instance.
   *
   * @param name The namespace name (e.g., "document", "folder", "organization")
   * @param relationDefinitions The list of relation definitions for this namespace
   */
  @JsonCreator
  public NamespaceModel(
      @JsonProperty("name") String name,
      @JsonProperty("relationDefinitions") List<RelationDefinitionModel> relationDefinitions) {
    this.name = Objects.requireNonNull(name, "Namespace name cannot be null");
    this.relationDefinitions =
        relationDefinitions != null ? new ArrayList<>(relationDefinitions) : new ArrayList<>();
  }

  /**
   * Gets the namespace name.
   *
   * @return The namespace name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the relation definitions.
   *
   * @return The list of relation definitions
   */
  public List<RelationDefinitionModel> getRelationDefinitions() {
    return new ArrayList<>(relationDefinitions);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NamespaceModel that = (NamespaceModel) o;
    return Objects.equals(name, that.name)
        && Objects.equals(relationDefinitions, that.relationDefinitions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, relationDefinitions);
  }

  @Override
  public String toString() {
    return "NamespaceModel{"
        + "name='"
        + name
        + '\''
        + ", relationDefinitions="
        + relationDefinitions
        + '}';
  }
}
