package com.descope.utils.model.fga;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for RelationQueryModel. */
public class RelationQueryModelTest {

  @Test
  @DisplayName("constructor - all fields provided - should create instance")
  public void constructor_allFieldsProvided_createsInstance() {
    // Given
    String resource = "document:report-123";
    String relationDefinition = "owner";
    String namespace = "document";
    String target = "user:alice@example.com";

    // When
    RelationQueryModel model =
        new RelationQueryModel(resource, relationDefinition, namespace, target);

    // Then
    assertNotNull(model);
    assertEquals(resource, model.getResource());
    assertEquals(relationDefinition, model.getRelationDefinition());
    assertEquals(namespace, model.getNamespace());
    assertEquals(target, model.getTarget());
  }

  @Test
  @DisplayName("constructor - all fields null - should create instance with null fields")
  public void constructor_allFieldsNull_createsInstanceWithNulls() {
    // When
    RelationQueryModel model = new RelationQueryModel(null, null, null, null);

    // Then
    assertNotNull(model);
    assertNull(model.getResource());
    assertNull(model.getRelationDefinition());
    assertNull(model.getNamespace());
    assertNull(model.getTarget());
  }

  @Test
  @DisplayName("constructor - partial fields - should create instance with mixed values")
  public void constructor_partialFields_createsInstanceWithMixedValues() {
    // When
    RelationQueryModel model = new RelationQueryModel("document:123", null, "document", null);

    // Then
    assertNotNull(model);
    assertEquals("document:123", model.getResource());
    assertNull(model.getRelationDefinition());
    assertEquals("document", model.getNamespace());
    assertNull(model.getTarget());
  }

  @Test
  @DisplayName("equals - same object - should return true")
  public void equals_sameObject_returnsTrue() {
    RelationQueryModel model =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");

    assertEquals(model, model);
  }

  @Test
  @DisplayName("equals - equal objects - should return true")
  public void equals_equalObjects_returnsTrue() {
    RelationQueryModel model1 =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");
    RelationQueryModel model2 =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");

    assertEquals(model1, model2);
    assertEquals(model2, model1);
  }

  @Test
  @DisplayName("equals - different resource - should return false")
  public void equals_differentResource_returnsFalse() {
    RelationQueryModel model1 =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");
    RelationQueryModel model2 =
        new RelationQueryModel("document:456", "owner", "document", "user:alice");

    assertNotEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - both with nulls - should return true")
  public void equals_bothWithNulls_returnsTrue() {
    RelationQueryModel model1 = new RelationQueryModel(null, null, null, null);
    RelationQueryModel model2 = new RelationQueryModel(null, null, null, null);

    assertEquals(model1, model2);
  }

  @Test
  @DisplayName("equals - null - should return false")
  public void equals_null_returnsFalse() {
    RelationQueryModel model =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");

    assertNotEquals(model, null);
  }

  @Test
  @DisplayName("equals - different class - should return false")
  public void equals_differentClass_returnsFalse() {
    RelationQueryModel model =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");

    assertNotEquals(model, "not a RelationQueryModel");
  }

  @Test
  @DisplayName("hashCode - equal objects - should have same hashCode")
  public void hashCode_equalObjects_sameHashCode() {
    RelationQueryModel model1 =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");
    RelationQueryModel model2 =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");

    assertEquals(model1.hashCode(), model2.hashCode());
  }

  @Test
  @DisplayName("toString - valid model - should contain key fields")
  public void toString_validModel_containsKeyFields() {
    RelationQueryModel model =
        new RelationQueryModel("document:123", "owner", "document", "user:alice");

    String result = model.toString();

    assertTrue(result.contains("document:123"));
    assertTrue(result.contains("owner"));
    assertTrue(result.contains("document"));
    assertTrue(result.contains("user:alice"));
  }

  @Test
  @DisplayName("toString - model with nulls - should handle null values")
  public void toString_modelWithNulls_handlesNulls() {
    RelationQueryModel model = new RelationQueryModel(null, "owner", null, "user:alice");

    String result = model.toString();

    assertNotNull(result);
    assertTrue(result.contains("owner"));
    assertTrue(result.contains("user:alice"));
  }
}
