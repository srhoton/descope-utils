package com.descope.utils.model.fga;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a query for checking FGA relations.
 *
 * <p>A relation query is used to check if specific authorization relationships exist. All fields
 * are optional to allow for flexible querying patterns.
 */
public class RelationQueryModel {

  private final String resource;
  private final String relationDefinition;
  private final String namespace;
  private final String target;

  /**
   * Creates a new RelationQueryModel instance.
   *
   * @param resource The resource identifier to query (optional)
   * @param relationDefinition The relation name to check (optional)
   * @param namespace The namespace to query (optional)
   * @param target The target/subject identifier to check (optional)
   */
  @JsonCreator
  public RelationQueryModel(
      @JsonProperty("resource") String resource,
      @JsonProperty("relationDefinition") String relationDefinition,
      @JsonProperty("namespace") String namespace,
      @JsonProperty("target") String target) {
    this.resource = resource;
    this.relationDefinition = relationDefinition;
    this.namespace = namespace;
    this.target = target;
  }

  /**
   * Gets the resource identifier.
   *
   * @return The resource identifier, or null if not specified
   */
  public String getResource() {
    return resource;
  }

  /**
   * Gets the relation definition name.
   *
   * @return The relation definition name, or null if not specified
   */
  public String getRelationDefinition() {
    return relationDefinition;
  }

  /**
   * Gets the namespace.
   *
   * @return The namespace, or null if not specified
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * Gets the target/subject identifier.
   *
   * @return The target/subject identifier, or null if not specified
   */
  public String getTarget() {
    return target;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RelationQueryModel that = (RelationQueryModel) o;
    return Objects.equals(resource, that.resource)
        && Objects.equals(relationDefinition, that.relationDefinition)
        && Objects.equals(namespace, that.namespace)
        && Objects.equals(target, that.target);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resource, relationDefinition, namespace, target);
  }

  @Override
  public String toString() {
    return "RelationQueryModel{"
        + "resource='"
        + resource
        + '\''
        + ", relationDefinition='"
        + relationDefinition
        + '\''
        + ", namespace='"
        + namespace
        + '\''
        + ", target='"
        + target
        + '\''
        + '}';
  }
}
