package com.descope.utils.model.fga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaResourceIdentifierModel. */
class FgaResourceIdentifierModelTest {

  @Test
  @DisplayName("constructor - valid fields - creates model with correct values")
  void constructor_validFields_createsModelWithCorrectValues() {
    FgaResourceIdentifierModel model = new FgaResourceIdentifierModel("doc1", "document");

    assertThat(model.getResourceId()).isEqualTo("doc1");
    assertThat(model.getResourceType()).isEqualTo("document");
  }

  @Test
  @DisplayName("constructor - null resourceId - throws NullPointerException")
  void constructor_nullResourceId_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaResourceIdentifierModel(null, "document"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Resource ID");
  }

  @Test
  @DisplayName("constructor - null resourceType - throws NullPointerException")
  void constructor_nullResourceType_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaResourceIdentifierModel("doc1", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Resource type");
  }

  @Test
  @DisplayName("equals - same values - returns true")
  void equals_sameValues_returnsTrue() {
    FgaResourceIdentifierModel a = new FgaResourceIdentifierModel("doc1", "document");
    FgaResourceIdentifierModel b = new FgaResourceIdentifierModel("doc1", "document");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  @DisplayName("toString - contains both fields")
  void toString_containsBothFields() {
    FgaResourceIdentifierModel model = new FgaResourceIdentifierModel("doc1", "document");

    assertThat(model.toString()).contains("doc1", "document");
  }
}
