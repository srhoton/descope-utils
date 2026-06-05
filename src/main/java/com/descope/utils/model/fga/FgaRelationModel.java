package com.descope.utils.model.fga;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Fine-Grained Authorization (FGA) relation using the new FGAService model.
 *
 * <p>A relation defines an authorization relationship between a target and a resource, both
 * identified by ID and type. For example: resource "doc1" of type "document" has relation "owner"
 * with target "user1" of type "user".
 */
public class FgaRelationModel {

  private final String resource;
  private final String resourceType;
  private final String relation;
  private final String target;
  private final String targetType;

  /**
   * Creates a new FgaRelationModel instance.
   *
   * @param resource The resource identifier (e.g., "doc1")
   * @param resourceType The resource type (e.g., "document")
   * @param relation The relation name (e.g., "owner", "viewer")
   * @param target The target identifier (e.g., "user1")
   * @param targetType The target type (e.g., "user", "organization")
   */
  @JsonCreator
  public FgaRelationModel(
      @JsonProperty("resource") String resource,
      @JsonProperty("resourceType") String resourceType,
      @JsonProperty("relation") String relation,
      @JsonProperty("target") String target,
      @JsonProperty("targetType") String targetType) {
    this.resource = Objects.requireNonNull(resource, "Resource cannot be null");
    this.resourceType = Objects.requireNonNull(resourceType, "Resource type cannot be null");
    this.relation = Objects.requireNonNull(relation, "Relation cannot be null");
    this.target = Objects.requireNonNull(target, "Target cannot be null");
    this.targetType = Objects.requireNonNull(targetType, "Target type cannot be null");
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
   * Gets the resource type.
   *
   * @return The resource type
   */
  public String getResourceType() {
    return resourceType;
  }

  /**
   * Gets the relation name.
   *
   * @return The relation name
   */
  public String getRelation() {
    return relation;
  }

  /**
   * Gets the target identifier.
   *
   * @return The target identifier
   */
  public String getTarget() {
    return target;
  }

  /**
   * Gets the target type.
   *
   * @return The target type
   */
  public String getTargetType() {
    return targetType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaRelationModel that = (FgaRelationModel) o;
    return Objects.equals(resource, that.resource)
        && Objects.equals(resourceType, that.resourceType)
        && Objects.equals(relation, that.relation)
        && Objects.equals(target, that.target)
        && Objects.equals(targetType, that.targetType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resource, resourceType, relation, target, targetType);
  }

  @Override
  public String toString() {
    return "FgaRelationModel{"
        + "resource='"
        + resource
        + '\''
        + ", resourceType='"
        + resourceType
        + '\''
        + ", relation='"
        + relation
        + '\''
        + ", target='"
        + target
        + '\''
        + ", targetType='"
        + targetType
        + '\''
        + '}';
  }
}
