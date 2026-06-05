package com.descope.utils.model.fga;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the result of an FGA authorization check for a single relation.
 *
 * <p>Flattens the SDK's FGACheckResult + FGACheckInfo + FGARelation into a single output model.
 */
public class FgaCheckResultModel {

  private final boolean allowed;
  private final String resource;
  private final String resourceType;
  private final String relation;
  private final String target;
  private final String targetType;
  private final boolean direct;

  /**
   * Creates a new FgaCheckResultModel instance.
   *
   * @param allowed Whether the relation is satisfied
   * @param resource The resource identifier
   * @param resourceType The resource type
   * @param relation The relation name
   * @param target The target identifier
   * @param targetType The target type
   * @param direct Whether the relation is directly satisfied (not derived)
   */
  @JsonCreator
  public FgaCheckResultModel(
      @JsonProperty("allowed") boolean allowed,
      @JsonProperty("resource") String resource,
      @JsonProperty("resourceType") String resourceType,
      @JsonProperty("relation") String relation,
      @JsonProperty("target") String target,
      @JsonProperty("targetType") String targetType,
      @JsonProperty("direct") boolean direct) {
    this.allowed = allowed;
    this.resource = resource;
    this.resourceType = resourceType;
    this.relation = relation;
    this.target = target;
    this.targetType = targetType;
    this.direct = direct;
  }

  /**
   * Returns whether the relation is allowed.
   *
   * @return true if the relation is satisfied
   */
  public boolean isAllowed() {
    return allowed;
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

  /**
   * Returns whether the relation is directly satisfied (not derived through inheritance).
   *
   * @return true if the relation is a direct (not computed) match
   */
  public boolean isDirect() {
    return direct;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaCheckResultModel that = (FgaCheckResultModel) o;
    return allowed == that.allowed
        && direct == that.direct
        && Objects.equals(resource, that.resource)
        && Objects.equals(resourceType, that.resourceType)
        && Objects.equals(relation, that.relation)
        && Objects.equals(target, that.target)
        && Objects.equals(targetType, that.targetType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowed, resource, resourceType, relation, target, targetType, direct);
  }

  @Override
  public String toString() {
    return "FgaCheckResultModel{"
        + "allowed="
        + allowed
        + ", resource='"
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
        + ", direct="
        + direct
        + '}';
  }
}
