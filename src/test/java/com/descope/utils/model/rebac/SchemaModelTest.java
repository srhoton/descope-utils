package com.descope.utils.model.rebac;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SchemaModelTest {

  @Test
  @DisplayName("constructor - valid parameters with name - creates schema")
  public void constructor_validParametersWithName_createsSchema() {
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", Arrays.asList("user"));
    NamespaceModel namespace = new NamespaceModel("document", Arrays.asList(relation));
    List<NamespaceModel> namespaces = Arrays.asList(namespace);

    SchemaModel schema = new SchemaModel("MySchema", namespaces);

    assertEquals("MySchema", schema.getName());
    assertEquals(1, schema.getNamespaces().size());
    assertEquals(namespaces, schema.getNamespaces());
  }

  @Test
  @DisplayName("constructor - valid parameters without name - creates schema")
  public void constructor_validParametersWithoutName_createsSchema() {
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", Arrays.asList("user"));
    NamespaceModel namespace = new NamespaceModel("document", Arrays.asList(relation));
    List<NamespaceModel> namespaces = Arrays.asList(namespace);

    SchemaModel schema = new SchemaModel(namespaces);

    assertNull(schema.getName());
    assertEquals(1, schema.getNamespaces().size());
    assertEquals(namespaces, schema.getNamespaces());
  }

  @Test
  @DisplayName("constructor - null name - creates schema with null name")
  public void constructor_nullName_createsSchemaWithNullName() {
    NamespaceModel namespace = new NamespaceModel("document", Collections.emptyList());
    List<NamespaceModel> namespaces = Arrays.asList(namespace);

    SchemaModel schema = new SchemaModel(null, namespaces);

    assertNull(schema.getName());
    assertEquals(1, schema.getNamespaces().size());
  }

  @Test
  @DisplayName("constructor - null namespaces - creates empty list")
  public void constructor_nullNamespaces_createsEmptyList() {
    SchemaModel schema = new SchemaModel("TestSchema", null);

    assertEquals("TestSchema", schema.getName());
    assertTrue(schema.getNamespaces().isEmpty());
  }

  @Test
  @DisplayName("constructor - empty namespaces - preserves empty list")
  public void constructor_emptyNamespaces_preservesEmptyList() {
    SchemaModel schema = new SchemaModel("EmptySchema", Collections.emptyList());

    assertEquals("EmptySchema", schema.getName());
    assertTrue(schema.getNamespaces().isEmpty());
  }

  @Test
  @DisplayName("getNamespaces - returns defensive copy - modifications don't affect original")
  public void getNamespaces_returnsDefensiveCopy_modificationsDoNotAffectOriginal() {
    NamespaceModel namespace = new NamespaceModel("document", Collections.emptyList());
    List<NamespaceModel> namespaces = Arrays.asList(namespace);
    SchemaModel schema = new SchemaModel("TestSchema", namespaces);

    List<NamespaceModel> retrieved = schema.getNamespaces();
    retrieved.add(new NamespaceModel("folder", Collections.emptyList()));

    assertEquals(1, schema.getNamespaces().size());
  }

  @Test
  @DisplayName("equals - same values - returns true")
  public void equals_sameValues_returnsTrue() {
    NamespaceModel namespace = new NamespaceModel("document", Collections.emptyList());
    List<NamespaceModel> namespaces = Arrays.asList(namespace);

    SchemaModel schema1 = new SchemaModel("TestSchema", namespaces);
    SchemaModel schema2 = new SchemaModel("TestSchema", namespaces);

    assertEquals(schema1, schema2);
  }

  @Test
  @DisplayName("equals - different names - returns false")
  public void equals_differentNames_returnsFalse() {
    NamespaceModel namespace = new NamespaceModel("document", Collections.emptyList());
    List<NamespaceModel> namespaces = Arrays.asList(namespace);

    SchemaModel schema1 = new SchemaModel("Schema1", namespaces);
    SchemaModel schema2 = new SchemaModel("Schema2", namespaces);

    assertNotEquals(schema1, schema2);
  }

  @Test
  @DisplayName("equals - different namespaces - returns false")
  public void equals_differentNamespaces_returnsFalse() {
    NamespaceModel namespace1 = new NamespaceModel("document", Collections.emptyList());
    NamespaceModel namespace2 = new NamespaceModel("folder", Collections.emptyList());

    SchemaModel schema1 = new SchemaModel("TestSchema", Arrays.asList(namespace1));
    SchemaModel schema2 = new SchemaModel("TestSchema", Arrays.asList(namespace2));

    assertNotEquals(schema1, schema2);
  }

  @Test
  @DisplayName("hashCode - same values - returns same hash")
  public void hashCode_sameValues_returnsSameHash() {
    NamespaceModel namespace = new NamespaceModel("document", Collections.emptyList());
    List<NamespaceModel> namespaces = Arrays.asList(namespace);

    SchemaModel schema1 = new SchemaModel("TestSchema", namespaces);
    SchemaModel schema2 = new SchemaModel("TestSchema", namespaces);

    assertEquals(schema1.hashCode(), schema2.hashCode());
  }

  @Test
  @DisplayName("toString - includes all fields")
  public void toString_includesAllFields() {
    NamespaceModel namespace = new NamespaceModel("document", Collections.emptyList());
    SchemaModel schema = new SchemaModel("TestSchema", Arrays.asList(namespace));

    String str = schema.toString();
    assertTrue(str.contains("TestSchema"));
    assertTrue(str.contains("document"));
  }
}
