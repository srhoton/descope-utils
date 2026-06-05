package com.descope.utils.model.fga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaResourceDetailsBatchModel. */
class FgaResourceDetailsBatchModelTest {

  @Test
  @DisplayName("constructor - valid list - creates model with correct count")
  void constructor_validList_createsModelWithCorrectCount() {
    FgaResourceDetailsModel detail = new FgaResourceDetailsModel("doc1", "document", "My Document");
    FgaResourceDetailsBatchModel batch = new FgaResourceDetailsBatchModel(List.of(detail));

    assertThat(batch.getCount()).isEqualTo(1);
    assertThat(batch.getDetails()).containsExactly(detail);
  }

  @Test
  @DisplayName("constructor - null list - throws NullPointerException")
  void constructor_nullList_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaResourceDetailsBatchModel(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("constructor - empty list - throws IllegalArgumentException")
  void constructor_emptyList_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new FgaResourceDetailsBatchModel(Collections.emptyList()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("empty");
  }

  @Test
  @DisplayName("getDetails - returns defensive copy")
  void getDetails_returnsDefensiveCopy() {
    FgaResourceDetailsModel detail = new FgaResourceDetailsModel("doc1", "document", "My Document");
    FgaResourceDetailsBatchModel batch = new FgaResourceDetailsBatchModel(List.of(detail));

    List<FgaResourceDetailsModel> copy = batch.getDetails();
    copy.clear();

    assertThat(batch.getDetails()).hasSize(1);
  }

  @Test
  @DisplayName("equals - same values - returns true")
  void equals_sameValues_returnsTrue() {
    FgaResourceDetailsModel detail = new FgaResourceDetailsModel("doc1", "document", "My Document");
    FgaResourceDetailsBatchModel a = new FgaResourceDetailsBatchModel(List.of(detail));
    FgaResourceDetailsBatchModel b = new FgaResourceDetailsBatchModel(List.of(detail));

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  @DisplayName("toString - contains detail values")
  void toString_containsDetailValues() {
    FgaResourceDetailsModel detail = new FgaResourceDetailsModel("doc1", "document", "My Document");
    FgaResourceDetailsBatchModel batch = new FgaResourceDetailsBatchModel(List.of(detail));

    assertThat(batch.toString()).contains("doc1");
  }
}
