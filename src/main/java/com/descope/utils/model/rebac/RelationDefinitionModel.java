package com.descope.utils.model.rebac;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a relation definition in a ReBAC schema.
 *
 * <p>A relation definition defines a type of relationship that can exist between subjects and
 * resources within a namespace.
 */
public class RelationDefinitionModel {

  private final String name;
  private final List<String> targetNamespaces;
  private final NodeModel complexDefinition;

  /**
   * Creates a new RelationDefinitionModel instance.
   *
   * @param name The name of the relation (e.g., "owner", "viewer", "editor")
   * @param targetNamespaces The list of target namespaces this relation can reference
   * @param complexDefinition The complex definition for computed relations
   */
  @JsonCreator
  public RelationDefinitionModel(
      @JsonProperty("name") String name,
      @JsonProperty("targetNamespaces") List<String> targetNamespaces,
      @JsonProperty("complexDefinition") NodeModel complexDefinition) {
    this.name = Objects.requireNonNull(name, "Relation name cannot be null");
    this.targetNamespaces =
        targetNamespaces != null ? new ArrayList<>(targetNamespaces) : new ArrayList<>();
    this.complexDefinition = complexDefinition;
  }

  /**
   * Gets the relation name.
   *
   * @return The relation name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the target namespaces.
   *
   * @return The list of target namespaces
   */
  public List<String> getTargetNamespaces() {
    return new ArrayList<>(targetNamespaces);
  }

  /**
   * Gets the complex definition for computed relations.
   *
   * @return The complex definition, or null if not a computed relation
   */
  public NodeModel getComplexDefinition() {
    return complexDefinition;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RelationDefinitionModel that = (RelationDefinitionModel) o;
    return Objects.equals(name, that.name)
        && Objects.equals(targetNamespaces, that.targetNamespaces)
        && Objects.equals(complexDefinition, that.complexDefinition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, targetNamespaces, complexDefinition);
  }

  @Override
  public String toString() {
    return "RelationDefinitionModel{"
        + "name='"
        + name
        + '\''
        + ", targetNamespaces="
        + targetNamespaces
        + ", complexDefinition="
        + complexDefinition
        + '}';
  }
}
