package com.descope.utils.model.fga;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Identifies an FGA resource for loading details.
 *
 * <p>Used as input to the load-fga-resource-details command.
 */
public class FgaResourceIdentifierModel {

  private final String resourceId;
  private final String resourceType;

  /**
   * Creates a new FgaResourceIdentifierModel.
   *
   * @param resourceId The resource identifier
   * @param resourceType The resource type
   */
  @JsonCreator
  public FgaResourceIdentifierModel(
      @JsonProperty("resourceId") String resourceId,
      @JsonProperty("resourceType") String resourceType) {
    this.resourceId = Objects.requireNonNull(resourceId, "Resource ID cannot be null");
    this.resourceType = Objects.requireNonNull(resourceType, "Resource type cannot be null");
  }

  /**
   * Gets the resource identifier.
   *
   * @return The resource identifier
   */
  public String getResourceId() {
    return resourceId;
  }

  /**
   * Gets the resource type.
   *
   * @return The resource type
   */
  public String getResourceType() {
    return resourceType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaResourceIdentifierModel that = (FgaResourceIdentifierModel) o;
    return Objects.equals(resourceId, that.resourceId)
        && Objects.equals(resourceType, that.resourceType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceId, resourceType);
  }

  @Override
  public String toString() {
    return "FgaResourceIdentifierModel{"
        + "resourceId='"
        + resourceId
        + '\''
        + ", resourceType='"
        + resourceType
        + '\''
        + '}';
  }
}
