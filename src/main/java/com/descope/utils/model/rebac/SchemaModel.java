package com.descope.utils.model.rebac;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a complete ReBAC (Relationship-Based Access Control) schema.
 *
 * <p>A schema defines the authorization model for an application, consisting of namespaces and
 * their associated relation definitions.
 */
public class SchemaModel {

  private final String name;
  private final List<NamespaceModel> namespaces;

  /**
   * Creates a new SchemaModel instance.
   *
   * @param name The schema name (optional, used for documentation)
   * @param namespaces The list of namespaces in the schema
   */
  @JsonCreator
  public SchemaModel(
      @JsonProperty("name") String name,
      @JsonProperty("namespaces") List<NamespaceModel> namespaces) {
    this.name = name;
    this.namespaces = namespaces != null ? new ArrayList<>(namespaces) : new ArrayList<>();
  }

  /**
   * Creates a new SchemaModel instance with only namespaces.
   *
   * @param namespaces The list of namespaces in the schema
   */
  public SchemaModel(List<NamespaceModel> namespaces) {
    this(null, namespaces);
  }

  /**
   * Gets the schema name.
   *
   * @return The schema name, or null if not set
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the namespaces.
   *
   * @return The list of namespaces
   */
  public List<NamespaceModel> getNamespaces() {
    return new ArrayList<>(namespaces);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SchemaModel that = (SchemaModel) o;
    return Objects.equals(name, that.name) && Objects.equals(namespaces, that.namespaces);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, namespaces);
  }

  @Override
  public String toString() {
    return "SchemaModel{" + "name='" + name + '\'' + ", namespaces=" + namespaces + '}';
  }
}
