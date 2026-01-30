package com.descope.utils.model.rebac;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NamespaceModelTest {

  @Test
  @DisplayName("constructor - valid parameters - creates namespace")
  public void constructor_validParameters_createsNamespace() {
    RelationDefinitionModel ownerRelation =
        new RelationDefinitionModel("owner", Arrays.asList("user"));
    RelationDefinitionModel viewerRelation =
        new RelationDefinitionModel("viewer", Arrays.asList("user", "group"));
    List<RelationDefinitionModel> relations = Arrays.asList(ownerRelation, viewerRelation);

    NamespaceModel namespace = new NamespaceModel("document", relations);

    assertEquals("document", namespace.getName());
    assertEquals(2, namespace.getRelationDefinitions().size());
    assertEquals(relations, namespace.getRelationDefinitions());
  }

  @Test
  @DisplayName("constructor - null name - throws NullPointerException")
  public void constructor_nullName_throwsNullPointerException() {
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", Arrays.asList("user"));
    List<RelationDefinitionModel> relations = Arrays.asList(relation);

    assertThrows(NullPointerException.class, () -> new NamespaceModel(null, relations));
  }

  @Test
  @DisplayName("constructor - null relationDefinitions - creates empty list")
  public void constructor_nullRelationDefinitions_createsEmptyList() {
    NamespaceModel namespace = new NamespaceModel("folder", null);

    assertEquals("folder", namespace.getName());
    assertTrue(namespace.getRelationDefinitions().isEmpty());
  }

  @Test
  @DisplayName("constructor - empty relationDefinitions - preserves empty list")
  public void constructor_emptyRelationDefinitions_preservesEmptyList() {
    NamespaceModel namespace = new NamespaceModel("organization", Collections.emptyList());

    assertEquals("organization", namespace.getName());
    assertTrue(namespace.getRelationDefinitions().isEmpty());
  }

  @Test
  @DisplayName(
      "getRelationDefinitions - returns defensive copy - modifications don't affect original")
  public void getRelationDefinitions_returnsDefensiveCopy_modificationsDoNotAffectOriginal() {
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", Arrays.asList("user"));
    List<RelationDefinitionModel> relations = Arrays.asList(relation);
    NamespaceModel namespace = new NamespaceModel("document", relations);

    List<RelationDefinitionModel> retrieved = namespace.getRelationDefinitions();
    retrieved.add(new RelationDefinitionModel("editor", Arrays.asList("user")));

    assertEquals(1, namespace.getRelationDefinitions().size());
  }

  @Test
  @DisplayName("equals - same values - returns true")
  public void equals_sameValues_returnsTrue() {
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", Arrays.asList("user"));
    List<RelationDefinitionModel> relations = Arrays.asList(relation);

    NamespaceModel namespace1 = new NamespaceModel("document", relations);
    NamespaceModel namespace2 = new NamespaceModel("document", relations);

    assertEquals(namespace1, namespace2);
  }

  @Test
  @DisplayName("equals - different names - returns false")
  public void equals_differentNames_returnsFalse() {
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", Arrays.asList("user"));
    List<RelationDefinitionModel> relations = Arrays.asList(relation);

    NamespaceModel namespace1 = new NamespaceModel("document", relations);
    NamespaceModel namespace2 = new NamespaceModel("folder", relations);

    assertNotEquals(namespace1, namespace2);
  }

  @Test
  @DisplayName("equals - different relation definitions - returns false")
  public void equals_differentRelationDefinitions_returnsFalse() {
    RelationDefinitionModel relation1 = new RelationDefinitionModel("owner", Arrays.asList("user"));
    RelationDefinitionModel relation2 =
        new RelationDefinitionModel("viewer", Arrays.asList("user"));

    NamespaceModel namespace1 = new NamespaceModel("document", Arrays.asList(relation1));
    NamespaceModel namespace2 = new NamespaceModel("document", Arrays.asList(relation2));

    assertNotEquals(namespace1, namespace2);
  }

  @Test
  @DisplayName("hashCode - same values - returns same hash")
  public void hashCode_sameValues_returnsSameHash() {
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", Arrays.asList("user"));
    List<RelationDefinitionModel> relations = Arrays.asList(relation);

    NamespaceModel namespace1 = new NamespaceModel("document", relations);
    NamespaceModel namespace2 = new NamespaceModel("document", relations);

    assertEquals(namespace1.hashCode(), namespace2.hashCode());
  }

  @Test
  @DisplayName("toString - includes all fields")
  public void toString_includesAllFields() {
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", Arrays.asList("user"));
    NamespaceModel namespace = new NamespaceModel("document", Arrays.asList(relation));

    String str = namespace.toString();
    assertTrue(str.contains("document"));
    assertTrue(str.contains("owner"));
  }
}
