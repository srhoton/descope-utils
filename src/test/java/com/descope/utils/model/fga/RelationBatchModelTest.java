package com.descope.utils.model.fga;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for RelationBatchModel. */
public class RelationBatchModelTest {

  @Test
  @DisplayName("constructor - valid relations list - should create instance")
  public void constructor_validRelationsList_createsInstance() {
    // Given
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    relations.add(new RelationTupleModel("document:456", "viewer", "document", "user:bob"));

    // When
    RelationBatchModel model = new RelationBatchModel(relations);

    // Then
    assertNotNull(model);
    assertEquals(2, model.getCount());
    assertEquals(2, model.getRelations().size());
    assertEquals(relations, model.getRelations());
  }

  @Test
  @DisplayName("constructor - single relation - should create instance")
  public void constructor_singleRelation_createsInstance() {
    // Given
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    // When
    RelationBatchModel model = new RelationBatchModel(relations);

    // Then
    assertNotNull(model);
    assertEquals(1, model.getCount());
    assertEquals(1, model.getRelations().size());
  }

  @Test
  @DisplayName("constructor - null relations - should throw NullPointerException")
  public void constructor_nullRelations_throwsException() {
    assertThrows(NullPointerException.class, () -> new RelationBatchModel(null));
  }

  @Test
  @DisplayName("constructor - empty relations list - should throw IllegalArgumentException")
  public void constructor_emptyRelationsList_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new RelationBatchModel(new ArrayList<>()));
  }

  @Test
  @DisplayName("getRelations - should return defensive copy")
  public void getRelations_returnsDefensiveCopy() {
    // Given
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    RelationBatchModel model = new RelationBatchModel(relations);

    // When
    List<RelationTupleModel> retrieved = model.getRelations();
    retrieved.add(new RelationTupleModel("document:456", "viewer", "document", "user:bob"));

    // Then - original model should not be affected
    assertEquals(1, model.getCount());
    assertEquals(1, model.getRelations().size());
  }

  @Test
  @DisplayName("constructor - should not be affected by external list modification")
  public void constructor_notAffectedByExternalListModification() {
    // Given
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    // When
    RelationBatchModel model = new RelationBatchModel(relations);
    relations.add(new RelationTupleModel("document:456", "viewer", "document", "user:bob"));

    // Then - model should not be affected
    assertEquals(1, model.getCount());
    assertEquals(1, model.getRelations().size());
  }

  @Test
  @DisplayName("equals - same object - should return true")
  public void equals_sameObject_returnsTrue() {
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    RelationBatchModel model = new RelationBatchModel(relations);

    assertEquals(model, model);
  }

  @Test
  @DisplayName("equals - equal objects - should return true")
  public void equals_equalObjects_returnsTrue() {
    List<RelationTupleModel> relations1 = new ArrayList<>();
    relations1.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    List<RelationTupleModel> relations2 = new ArrayList<>();
    relations2.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    RelationBatchModel model1 = new RelationBatchModel(relations1);
    RelationBatchModel model2 = new RelationBatchModel(relations2);

    assertEquals(model1, model2);
    assertEquals(model2, model1);
  }

  @Test
  @DisplayName("equals - different relations - should return false")
  public void equals_differentRelations_returnsFalse() {
    List<RelationTupleModel> relations1 = new ArrayList<>();
    relations1.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    List<RelationTupleModel> relations2 = new ArrayList<>();
    relations2.add(new RelationTupleModel("document:456", "viewer", "document", "user:bob"));

    RelationBatchModel model1 = new RelationBatchModel(relations1);
    RelationBatchModel model2 = new RelationBatchModel(relations2);

    assertNotEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - null - should return false")
  public void equals_null_returnsFalse() {
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    RelationBatchModel model = new RelationBatchModel(relations);

    assertNotEquals(model, null);
  }

  @Test
  @DisplayName("hashCode - equal objects - should have same hashCode")
  public void hashCode_equalObjects_sameHashCode() {
    List<RelationTupleModel> relations1 = new ArrayList<>();
    relations1.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    List<RelationTupleModel> relations2 = new ArrayList<>();
    relations2.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));

    RelationBatchModel model1 = new RelationBatchModel(relations1);
    RelationBatchModel model2 = new RelationBatchModel(relations2);

    assertEquals(model1.hashCode(), model2.hashCode());
  }

  @Test
  @DisplayName("toString - valid model - should contain key information")
  public void toString_validModel_containsKeyInformation() {
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    relations.add(new RelationTupleModel("document:456", "viewer", "document", "user:bob"));

    RelationBatchModel model = new RelationBatchModel(relations);
    String result = model.toString();

    assertTrue(result.contains("count"));
    assertTrue(result.contains("2"));
    assertTrue(result.contains("relations"));
  }

  @Test
  @DisplayName("getCount - should return correct count")
  public void getCount_returnsCorrectCount() {
    List<RelationTupleModel> relations = new ArrayList<>();
    relations.add(new RelationTupleModel("document:123", "owner", "document", "user:alice"));
    relations.add(new RelationTupleModel("document:456", "viewer", "document", "user:bob"));
    relations.add(new RelationTupleModel("document:789", "editor", "document", "user:charlie"));

    RelationBatchModel model = new RelationBatchModel(relations);

    assertEquals(3, model.getCount());
  }
}
