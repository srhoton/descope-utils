package com.descope.utils.model.fga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaResourceIdentifierBatchModel. */
class FgaResourceIdentifierBatchModelTest {

  @Test
  @DisplayName("constructor - valid list - creates model with correct count")
  void constructor_validList_createsModelWithCorrectCount() {
    FgaResourceIdentifierModel id = new FgaResourceIdentifierModel("doc1", "document");
    FgaResourceIdentifierBatchModel batch = new FgaResourceIdentifierBatchModel(List.of(id));

    assertThat(batch.getCount()).isEqualTo(1);
    assertThat(batch.getIdentifiers()).containsExactly(id);
  }

  @Test
  @DisplayName("constructor - null list - throws NullPointerException")
  void constructor_nullList_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaResourceIdentifierBatchModel(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("constructor - empty list - throws IllegalArgumentException")
  void constructor_emptyList_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new FgaResourceIdentifierBatchModel(Collections.emptyList()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("empty");
  }

  @Test
  @DisplayName("getIdentifiers - returns defensive copy")
  void getIdentifiers_returnsDefensiveCopy() {
    FgaResourceIdentifierModel id = new FgaResourceIdentifierModel("doc1", "document");
    FgaResourceIdentifierBatchModel batch = new FgaResourceIdentifierBatchModel(List.of(id));

    List<FgaResourceIdentifierModel> copy = batch.getIdentifiers();
    copy.clear();

    assertThat(batch.getIdentifiers()).hasSize(1);
  }

  @Test
  @DisplayName("equals - same values - returns true")
  void equals_sameValues_returnsTrue() {
    FgaResourceIdentifierModel id = new FgaResourceIdentifierModel("doc1", "document");
    FgaResourceIdentifierBatchModel a = new FgaResourceIdentifierBatchModel(List.of(id));
    FgaResourceIdentifierBatchModel b = new FgaResourceIdentifierBatchModel(List.of(id));

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  @DisplayName("toString - contains identifiers")
  void toString_containsIdentifiers() {
    FgaResourceIdentifierModel id = new FgaResourceIdentifierModel("doc1", "document");
    FgaResourceIdentifierBatchModel batch = new FgaResourceIdentifierBatchModel(List.of(id));

    assertThat(batch.toString()).contains("doc1");
  }
}
