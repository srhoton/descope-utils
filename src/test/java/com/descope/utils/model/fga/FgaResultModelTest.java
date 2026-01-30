package com.descope.utils.model.fga;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaResultModel. */
public class FgaResultModelTest {

  @Test
  @DisplayName("constructor - query results with relations - should create instance")
  public void constructor_queryResultsWithRelations_createsInstance() {
    // Given
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    relations.add(new RelationTupleModel("document:123", "viewer", "document", "user:bob"));

    // When
    FgaResultModel model = new FgaResultModel(relations);

    // Then
    assertNotNull(model);
    assertEquals(2, model.getCount());
    assertEquals(2, model.getRelations().size());
    assertNull(model.getExists());
  }

  @Test
  @DisplayName("constructor - query results with empty list - should create instance")
  public void constructor_queryResultsEmpty_createsInstance() {
    // Given
    List<RelationTupleModel> relations = new ArrayList<>();

    // When
    FgaResultModel model = new FgaResultModel(relations);

    // Then
    assertNotNull(model);
    assertEquals(0, model.getCount());
    assertEquals(0, model.getRelations().size());
    assertNull(model.getExists());
  }

  @Test
  @DisplayName("constructor - query results with null - should create empty instance")
  public void constructor_queryResultsNull_createsEmptyInstance() {
    // When
    FgaResultModel model = new FgaResultModel((List<RelationTupleModel>) null);

    // Then
    assertNotNull(model);
    assertEquals(0, model.getCount());
    assertEquals(0, model.getRelations().size());
    assertNull(model.getExists());
  }

  @Test
  @DisplayName("constructor - check result exists with relation - should create instance")
  public void constructor_checkResultExistsWithRelation_createsInstance() {
    // Given
    RelationTupleModel relation =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    // When
    FgaResultModel model = new FgaResultModel(true, relation);

    // Then
    assertNotNull(model);
    assertTrue(model.getExists());
    assertEquals(1, model.getCount());
    assertEquals(1, model.getRelations().size());
    assertEquals(relation, model.getRelations().get(0));
  }

  @Test
  @DisplayName("constructor - check result not exists - should create instance")
  public void constructor_checkResultNotExists_createsInstance() {
    // When
    FgaResultModel model = new FgaResultModel(false, null);

    // Then
    assertNotNull(model);
    assertFalse(model.getExists());
    assertEquals(0, model.getCount());
    assertEquals(0, model.getRelations().size());
  }

  @Test
  @DisplayName("constructor - full constructor - should create instance")
  public void constructor_fullConstructor_createsInstance() {
    // Given
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    // When
    FgaResultModel model = new FgaResultModel(1, relations, true);

    // Then
    assertNotNull(model);
    assertEquals(1, model.getCount());
    assertEquals(1, model.getRelations().size());
    assertTrue(model.getExists());
  }

  @Test
  @DisplayName("getRelations - should return defensive copy")
  public void getRelations_returnsDefensiveCopy() {
    // Given
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    FgaResultModel model = new FgaResultModel(relations);

    // When
    List<RelationTupleModel> retrieved = model.getRelations();
    retrieved.add(new RelationTupleModel("document:456", "viewer", "document", "user:bob"));

    // Then - original model should not be affected
    assertEquals(1, model.getCount());
    assertEquals(1, model.getRelations().size());
  }

  @Test
  @DisplayName("equals - same object - should return true")
  public void equals_sameObject_returnsTrue() {
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    FgaResultModel model = new FgaResultModel(relations);

    assertEquals(model, model);
  }

  @Test
  @DisplayName("equals - equal query results - should return true")
  public void equals_equalQueryResults_returnsTrue() {
    List<RelationTupleModel> relations1 = new ArrayList<>();
    relations1.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    List<RelationTupleModel> relations2 = new ArrayList<>();
    relations2.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    FgaResultModel model1 = new FgaResultModel(relations1);
    FgaResultModel model2 = new FgaResultModel(relations2);

    assertEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - equal check results - should return true")
  public void equals_equalCheckResults_returnsTrue() {
    RelationTupleModel relation =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    FgaResultModel model1 = new FgaResultModel(true, relation);
    FgaResultModel model2 = new FgaResultModel(true, relation);

    assertEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - different exists value - should return false")
  public void equals_differentExistsValue_returnsFalse() {
    RelationTupleModel relation =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    FgaResultModel model1 = new FgaResultModel(true, relation);
    FgaResultModel model2 = new FgaResultModel(false, null);

    assertNotEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - different count - should return false")
  public void equals_differentCount_returnsFalse() {
    List<RelationTupleModel> relations1 = new ArrayList<>();
    relations1.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    List<RelationTupleModel> relations2 = new ArrayList<>();
    relations2.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    relations2.add(new RelationTupleModel("document:456", "viewer", "document", "user:bob"));

    FgaResultModel model1 = new FgaResultModel(relations1);
    FgaResultModel model2 = new FgaResultModel(relations2);

    assertNotEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - null - should return false")
  public void equals_null_returnsFalse() {
    FgaResultModel model = new FgaResultModel(new ArrayList<>());

    assertNotEquals(model, null);
  }

  @Test
  @DisplayName("hashCode - equal objects - should have same hashCode")
  public void hashCode_equalObjects_sameHashCode() {
    List<RelationTupleModel> relations1 = new ArrayList<>();
    relations1.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    List<RelationTupleModel> relations2 = new ArrayList<>();
    relations2.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    FgaResultModel model1 = new FgaResultModel(relations1);
    FgaResultModel model2 = new FgaResultModel(relations2);

    assertEquals(model1.hashCode(), model2.hashCode());
  }

  @Test
  @DisplayName("toString - query result - should contain key fields")
  public void toString_queryResult_containsKeyFields() {
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    FgaResultModel model = new FgaResultModel(relations);

    String result = model.toString();

    assertTrue(result.contains("count"));
    assertTrue(result.contains("1"));
    assertTrue(result.contains("relations"));
  }

  @Test
  @DisplayName("toString - check result - should contain exists field")
  public void toString_checkResult_containsExistsField() {
    RelationTupleModel relation =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");
    FgaResultModel model = new FgaResultModel(true, relation);

    String result = model.toString();

    assertTrue(result.contains("exists"));
    assertTrue(result.contains("true"));
  }
}
