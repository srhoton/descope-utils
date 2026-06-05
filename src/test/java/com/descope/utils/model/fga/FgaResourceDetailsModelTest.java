package com.descope.utils.model.fga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaResourceDetailsModel. */
class FgaResourceDetailsModelTest {

  @Test
  @DisplayName("constructor - valid fields - creates model with correct values")
  void constructor_validFields_createsModelWithCorrectValues() {
    FgaResourceDetailsModel model = new FgaResourceDetailsModel("doc1", "document", "My Document");

    assertThat(model.getResourceId()).isEqualTo("doc1");
    assertThat(model.getResourceType()).isEqualTo("document");
    assertThat(model.getDisplayName()).isEqualTo("My Document");
  }

  @Test
  @DisplayName("constructor - null displayName - is allowed (optional field)")
  void constructor_nullDisplayName_isAllowed() {
    FgaResourceDetailsModel model = new FgaResourceDetailsModel("doc1", "document", null);

    assertThat(model.getDisplayName()).isNull();
  }

  @Test
  @DisplayName("constructor - null resourceId - throws NullPointerException")
  void constructor_nullResourceId_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaResourceDetailsModel(null, "document", "My Document"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Resource ID");
  }

  @Test
  @DisplayName("equals - same values - returns true")
  void equals_sameValues_returnsTrue() {
    FgaResourceDetailsModel a = new FgaResourceDetailsModel("doc1", "document", "My Document");
    FgaResourceDetailsModel b = new FgaResourceDetailsModel("doc1", "document", "My Document");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  @DisplayName("toString - contains all fields")
  void toString_containsAllFields() {
    FgaResourceDetailsModel model = new FgaResourceDetailsModel("doc1", "document", "My Document");

    assertThat(model.toString()).contains("doc1", "document", "My Document");
  }
}
