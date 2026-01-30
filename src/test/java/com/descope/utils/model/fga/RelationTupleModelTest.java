package com.descope.utils.model.fga;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for RelationTupleModel. */
public class RelationTupleModelTest {

  @Test
  @DisplayName("constructor - valid inputs - should create instance with all fields")
  public void constructor_validInputs_createsInstance() {
    // Given
    String resource = "document:report-123";
    String relationDefinition = "owner";
    String namespace = "document";
    String target = "user:alice@example.com";

    // When
    RelationTupleModel model =
        new RelationTupleModel(resource, relationDefinition, namespace, target);

    // Then
    assertNotNull(model);
    assertEquals(resource, model.getResource());
    assertEquals(relationDefinition, model.getRelationDefinition());
    assertEquals(namespace, model.getNamespace());
    assertEquals(target, model.getTarget());
  }

  @Test
  @DisplayName("constructor - null resource - should throw NullPointerException")
  public void constructor_nullResource_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new RelationTupleModel(null, "owner", "document", "user:alice"));
  }

  @Test
  @DisplayName("constructor - null relationDefinition - should throw NullPointerException")
  public void constructor_nullRelationDefinition_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new RelationTupleModel("document:123", null, "document", "user:alice"));
  }

  @Test
  @DisplayName("constructor - null namespace - should throw NullPointerException")
  public void constructor_nullNamespace_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new RelationTupleModel("document:123", "owner", null, "user:alice"));
  }

  @Test
  @DisplayName("constructor - null target - should throw NullPointerException")
  public void constructor_nullTarget_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new RelationTupleModel("document:123", "owner", "document", null));
  }

  @Test
  @DisplayName("equals - same object - should return true")
  public void equals_sameObject_returnsTrue() {
    RelationTupleModel model =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    assertEquals(model, model);
  }

  @Test
  @DisplayName("equals - equal objects - should return true")
  public void equals_equalObjects_returnsTrue() {
    RelationTupleModel model1 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");
    RelationTupleModel model2 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    assertEquals(model1, model2);
    assertEquals(model2, model1);
  }

  @Test
  @DisplayName("equals - different resource - should return false")
  public void equals_differentResource_returnsFalse() {
    RelationTupleModel model1 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");
    RelationTupleModel model2 =
        new RelationTupleModel("document:456", "owner", "document", "user:alice");

    assertNotEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - different relation - should return false")
  public void equals_differentRelation_returnsFalse() {
    RelationTupleModel model1 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");
    RelationTupleModel model2 =
        new RelationTupleModel("document:123", "viewer", "document", "user:alice");

    assertNotEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - different namespace - should return false")
  public void equals_differentNamespace_returnsFalse() {
    RelationTupleModel model1 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");
    RelationTupleModel model2 =
        new RelationTupleModel("document:123", "owner", "file", "user:alice");

    assertNotEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - different target - should return false")
  public void equals_differentTarget_returnsFalse() {
    RelationTupleModel model1 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");
    RelationTupleModel model2 =
        new RelationTupleModel("document:123", "owner", "document", "user:bob");

    assertNotEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - null - should return false")
  public void equals_null_returnsFalse() {
    RelationTupleModel model =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    assertNotEquals(model, null);
  }

  @Test
  @DisplayName("equals - different class - should return false")
  public void equals_differentClass_returnsFalse() {
    RelationTupleModel model =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    assertNotEquals(model, "not a RelationTupleModel");
  }

  @Test
  @DisplayName("hashCode - equal objects - should have same hashCode")
  public void hashCode_equalObjects_samHashCode() {
    RelationTupleModel model1 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");
    RelationTupleModel model2 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    assertEquals(model1.hashCode(), model2.hashCode());
  }

  @Test
  @DisplayName("hashCode - different objects - likely different hashCode")
  public void hashCode_differentObjects_likelyDifferentHashCode() {
    RelationTupleModel model1 =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");
    RelationTupleModel model2 =
        new RelationTupleModel("document:456", "viewer", "file", "user:bob");

    // Note: hashCode can theoretically collide, but unlikely with different values
    assertNotEquals(model1.hashCode(), model2.hashCode());
  }

  @Test
  @DisplayName("toString - valid model - should contain key fields")
  public void toString_validModel_containsKeyFields() {
    RelationTupleModel model =
        new RelationTupleModel("document:123", "owner", "document", "user:alice");

    String result = model.toString();

    assertTrue(result.contains("document:123"));
    assertTrue(result.contains("owner"));
    assertTrue(result.contains("document"));
    assertTrue(result.contains("user:alice"));
  }
}
