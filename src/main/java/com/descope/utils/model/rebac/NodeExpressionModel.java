package com.descope.utils.model.rebac;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a node expression in a ReBAC schema complex definition.
 *
 * <p>Node expressions define how relations are resolved, supporting self references, target sets,
 * and relation traversals.
 */
public class NodeExpressionModel {

  private final String neType;
  private final String relationDefinition;
  private final String relationDefinitionNamespace;
  private final String targetRelationDefinition;
  private final String targetRelationDefinitionNamespace;

  /**
   * Creates a new NodeExpressionModel instance.
   *
   * @param neType The expression type (self, targetSet, relationLeft, relationRight)
   * @param relationDefinition The relation definition name
   * @param relationDefinitionNamespace The namespace of the relation definition
   * @param targetRelationDefinition The target relation definition name
   * @param targetRelationDefinitionNamespace The namespace of the target relation definition
   */
  @JsonCreator
  public NodeExpressionModel(
      @JsonProperty("neType") String neType,
      @JsonProperty("relationDefinition") String relationDefinition,
      @JsonProperty("relationDefinitionNamespace") String relationDefinitionNamespace,
      @JsonProperty("targetRelationDefinition") String targetRelationDefinition,
      @JsonProperty("targetRelationDefinitionNamespace") String targetRelationDefinitionNamespace) {
    this.neType = neType;
    this.relationDefinition = relationDefinition;
    this.relationDefinitionNamespace = relationDefinitionNamespace;
    this.targetRelationDefinition = targetRelationDefinition;
    this.targetRelationDefinitionNamespace = targetRelationDefinitionNamespace;
  }

  public String getNeType() {
    return neType;
  }

  public String getRelationDefinition() {
    return relationDefinition;
  }

  public String getRelationDefinitionNamespace() {
    return relationDefinitionNamespace;
  }

  public String getTargetRelationDefinition() {
    return targetRelationDefinition;
  }

  public String getTargetRelationDefinitionNamespace() {
    return targetRelationDefinitionNamespace;
  }
}
