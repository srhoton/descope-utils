package com.descope.utils.model.fga;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Fine-Grained Authorization (FGA) relation tuple.
 *
 * <p>A relation tuple defines an authorization relationship between a target (subject) and a
 * resource. For example: "user:alice@example.com" has "owner" relation to "document:report-123".
 */
public class RelationTupleModel {

  private final String resource;
  private final String relationDefinition;
  private final String namespace;
  private final String target;

  /**
   * Creates a new RelationTupleModel instance.
   *
   * @param resource The resource identifier (e.g., "document:report-123")
   * @param relationDefinition The relation name (e.g., "owner", "viewer")
   * @param namespace The namespace for the resource
   * @param target The target/subject identifier (e.g., "user:alice@example.com")
   */
  @JsonCreator
  public RelationTupleModel(
      @JsonProperty("resource") String resource,
      @JsonProperty("relationDefinition") String relationDefinition,
      @JsonProperty("namespace") String namespace,
      @JsonProperty("target") String target) {
    this.resource = Objects.requireNonNull(resource, "Resource cannot be null");
    this.relationDefinition =
        Objects.requireNonNull(relationDefinition, "Relation definition cannot be null");
    this.namespace = Objects.requireNonNull(namespace, "Namespace cannot be null");
    this.target = Objects.requireNonNull(target, "Target cannot be null");
  }

  /**
   * Gets the resource identifier.
   *
   * @return The resource identifier
   */
  public String getResource() {
    return resource;
  }

  /**
   * Gets the relation definition name.
   *
   * @return The relation definition name
   */
  public String getRelationDefinition() {
    return relationDefinition;
  }

  /**
   * Gets the namespace.
   *
   * @return The namespace
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * Gets the target/subject identifier.
   *
   * @return The target/subject identifier
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
    RelationTupleModel that = (RelationTupleModel) o;
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
    return "RelationTupleModel{"
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
