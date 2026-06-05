package com.descope.utils.model.fga;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents display metadata for an FGA resource.
 *
 * <p>Used for both saving and loading resource details (human-readable display names for UI).
 */
public class FgaResourceDetailsModel {

  private final String resourceId;
  private final String resourceType;
  private final String displayName;

  /**
   * Creates a new FgaResourceDetailsModel.
   *
   * @param resourceId The resource identifier
   * @param resourceType The resource type
   * @param displayName The human-readable display name
   */
  @JsonCreator
  public FgaResourceDetailsModel(
      @JsonProperty("resourceId") String resourceId,
      @JsonProperty("resourceType") String resourceType,
      @JsonProperty("displayName") String displayName) {
    this.resourceId = Objects.requireNonNull(resourceId, "Resource ID cannot be null");
    this.resourceType = Objects.requireNonNull(resourceType, "Resource type cannot be null");
    this.displayName = displayName;
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

  /**
   * Gets the display name.
   *
   * @return The human-readable display name (may be null)
   */
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaResourceDetailsModel that = (FgaResourceDetailsModel) o;
    return Objects.equals(resourceId, that.resourceId)
        && Objects.equals(resourceType, that.resourceType)
        && Objects.equals(displayName, that.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceId, resourceType, displayName);
  }

  @Override
  public String toString() {
    return "FgaResourceDetailsModel{"
        + "resourceId='"
        + resourceId
        + '\''
        + ", resourceType='"
        + resourceType
        + '\''
        + ", displayName='"
        + displayName
        + '\''
        + '}';
  }
}
