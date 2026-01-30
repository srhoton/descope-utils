package com.descope.utils.model.rebac;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a node in a ReBAC schema complex definition.
 *
 * <p>Nodes can represent composite operations (union, intersect, sub) or leaf expressions.
 */
public class NodeModel {

  private final String nType;
  private final List<NodeModel> children;
  private final NodeExpressionModel expression;

  /**
   * Creates a new NodeModel instance.
   *
   * @param nType The node type (child, union, intersect, sub)
   * @param children Child nodes for composite operations
   * @param expression The expression for leaf nodes
   */
  @JsonCreator
  public NodeModel(
      @JsonProperty("nType") String nType,
      @JsonProperty("children") List<NodeModel> children,
      @JsonProperty("expression") NodeExpressionModel expression) {
    this.nType = nType;
    this.children = children != null ? new ArrayList<>(children) : null;
    this.expression = expression;
  }

  public String getNType() {
    return nType;
  }

  public List<NodeModel> getChildren() {
    return children != null ? new ArrayList<>(children) : null;
  }

  public NodeExpressionModel getExpression() {
    return expression;
  }
}
