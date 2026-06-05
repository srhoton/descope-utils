package com.descope.utils.model.fga;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an FGA schema in AuthZ 1.0 DSL format.
 *
 * <p>Example DSL:
 *
 * <pre>{@code
 * model AuthZ 1.0
 * type user
 * type document
 *   relation owner: user
 *   relation viewer: user
 *   permission can_edit: owner
 * }</pre>
 */
public class FgaSchemaModel {

  private final String dsl;

  /**
   * Creates a new FgaSchemaModel.
   *
   * @param dsl The AuthZ 1.0 DSL schema definition
   */
  @JsonCreator
  public FgaSchemaModel(@JsonProperty("dsl") String dsl) {
    this.dsl = Objects.requireNonNull(dsl, "DSL cannot be null");
  }

  /**
   * Gets the DSL schema definition.
   *
   * @return The DSL string
   */
  public String getDsl() {
    return dsl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FgaSchemaModel that = (FgaSchemaModel) o;
    return Objects.equals(dsl, that.dsl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dsl);
  }

  @Override
  public String toString() {
    return "FgaSchemaModel{dsl='" + dsl + "'}";
  }
}
