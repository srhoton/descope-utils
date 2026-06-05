package com.descope.utils.model.fga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaRelationModel. */
class FgaRelationModelTest {

  @Test
  @DisplayName("constructor - valid fields - creates model with correct values")
  void constructor_validFields_createsModelWithCorrectValues() {
    FgaRelationModel model = new FgaRelationModel("doc1", "document", "owner", "user1", "user");

    assertThat(model.getResource()).isEqualTo("doc1");
    assertThat(model.getResourceType()).isEqualTo("document");
    assertThat(model.getRelation()).isEqualTo("owner");
    assertThat(model.getTarget()).isEqualTo("user1");
    assertThat(model.getTargetType()).isEqualTo("user");
  }

  @Test
  @DisplayName("constructor - null resource - throws NullPointerException")
  void constructor_nullResource_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaRelationModel(null, "document", "owner", "user1", "user"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Resource");
  }

  @Test
  @DisplayName("constructor - null resourceType - throws NullPointerException")
  void constructor_nullResourceType_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaRelationModel("doc1", null, "owner", "user1", "user"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Resource type");
  }

  @Test
  @DisplayName("constructor - null target - throws NullPointerException")
  void constructor_nullTarget_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaRelationModel("doc1", "document", "owner", null, "user"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Target");
  }

  @Test
  @DisplayName("equals - same values - returns true")
  void equals_sameValues_returnsTrue() {
    FgaRelationModel a = new FgaRelationModel("doc1", "document", "owner", "user1", "user");
    FgaRelationModel b = new FgaRelationModel("doc1", "document", "owner", "user1", "user");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  @DisplayName("equals - different resource - returns false")
  void equals_differentResource_returnsFalse() {
    FgaRelationModel a = new FgaRelationModel("doc1", "document", "owner", "user1", "user");
    FgaRelationModel b = new FgaRelationModel("doc2", "document", "owner", "user1", "user");

    assertThat(a).isNotEqualTo(b);
  }

  @Test
  @DisplayName("toString - returns string with all fields")
  void toString_returnsStringWithAllFields() {
    FgaRelationModel model = new FgaRelationModel("doc1", "document", "owner", "user1", "user");
    String str = model.toString();

    assertThat(str).contains("doc1", "document", "owner", "user1", "user");
  }
}
