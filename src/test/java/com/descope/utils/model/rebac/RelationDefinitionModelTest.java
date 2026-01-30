package com.descope.utils.model.rebac;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RelationDefinitionModelTest {

  @Test
  @DisplayName("constructor - valid parameters - creates relation definition")
  public void constructor_validParameters_createsRelationDefinition() {
    List<String> targets = Arrays.asList("user", "group");
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", targets);

    assertEquals("owner", relation.getName());
    assertEquals(targets, relation.getTargetNamespaces());
  }

  @Test
  @DisplayName("constructor - null name - throws NullPointerException")
  public void constructor_nullName_throwsNullPointerException() {
    List<String> targets = Arrays.asList("user");
    assertThrows(NullPointerException.class, () -> new RelationDefinitionModel(null, targets));
  }

  @Test
  @DisplayName("constructor - null targetNamespaces - creates empty list")
  public void constructor_nullTargetNamespaces_createsEmptyList() {
    RelationDefinitionModel relation = new RelationDefinitionModel("viewer", null);

    assertEquals("viewer", relation.getName());
    assertTrue(relation.getTargetNamespaces().isEmpty());
  }

  @Test
  @DisplayName("constructor - empty targetNamespaces - preserves empty list")
  public void constructor_emptyTargetNamespaces_preservesEmptyList() {
    RelationDefinitionModel relation =
        new RelationDefinitionModel("editor", Collections.emptyList());

    assertEquals("editor", relation.getName());
    assertTrue(relation.getTargetNamespaces().isEmpty());
  }

  @Test
  @DisplayName("getTargetNamespaces - returns defensive copy - modifications don't affect original")
  public void getTargetNamespaces_returnsDefensiveCopy_modificationsDoNotAffectOriginal() {
    List<String> targets = Arrays.asList("user", "group");
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", targets);

    List<String> retrieved = relation.getTargetNamespaces();
    retrieved.add("organization");

    assertEquals(2, relation.getTargetNamespaces().size());
    assertFalse(relation.getTargetNamespaces().contains("organization"));
  }

  @Test
  @DisplayName("equals - same values - returns true")
  public void equals_sameValues_returnsTrue() {
    List<String> targets = Arrays.asList("user", "group");
    RelationDefinitionModel relation1 = new RelationDefinitionModel("owner", targets);
    RelationDefinitionModel relation2 = new RelationDefinitionModel("owner", targets);

    assertEquals(relation1, relation2);
  }

  @Test
  @DisplayName("equals - different names - returns false")
  public void equals_differentNames_returnsFalse() {
    List<String> targets = Arrays.asList("user");
    RelationDefinitionModel relation1 = new RelationDefinitionModel("owner", targets);
    RelationDefinitionModel relation2 = new RelationDefinitionModel("viewer", targets);

    assertNotEquals(relation1, relation2);
  }

  @Test
  @DisplayName("equals - different target namespaces - returns false")
  public void equals_differentTargetNamespaces_returnsFalse() {
    RelationDefinitionModel relation1 = new RelationDefinitionModel("owner", Arrays.asList("user"));
    RelationDefinitionModel relation2 =
        new RelationDefinitionModel("owner", Arrays.asList("group"));

    assertNotEquals(relation1, relation2);
  }

  @Test
  @DisplayName("hashCode - same values - returns same hash")
  public void hashCode_sameValues_returnsSameHash() {
    List<String> targets = Arrays.asList("user", "group");
    RelationDefinitionModel relation1 = new RelationDefinitionModel("owner", targets);
    RelationDefinitionModel relation2 = new RelationDefinitionModel("owner", targets);

    assertEquals(relation1.hashCode(), relation2.hashCode());
  }

  @Test
  @DisplayName("toString - includes all fields")
  public void toString_includesAllFields() {
    List<String> targets = Arrays.asList("user", "group");
    RelationDefinitionModel relation = new RelationDefinitionModel("owner", targets);

    String str = relation.toString();
    assertTrue(str.contains("owner"));
    assertTrue(str.contains("user"));
    assertTrue(str.contains("group"));
  }
}
