package com.descope.utils.model.fga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaRelationBatchModel. */
class FgaRelationBatchModelTest {

  @Test
  @DisplayName("constructor - valid list - creates model with correct count")
  void constructor_validList_createsModelWithCorrectCount() {
    FgaRelationModel rel = new FgaRelationModel("doc1", "document", "owner", "user1", "user");
    FgaRelationBatchModel batch = new FgaRelationBatchModel(List.of(rel));

    assertThat(batch.getCount()).isEqualTo(1);
    assertThat(batch.getRelations()).containsExactly(rel);
  }

  @Test
  @DisplayName("constructor - null list - throws NullPointerException")
  void constructor_nullList_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaRelationBatchModel(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("constructor - empty list - throws IllegalArgumentException")
  void constructor_emptyList_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new FgaRelationBatchModel(Collections.emptyList()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("empty");
  }

  @Test
  @DisplayName("getRelations - returns defensive copy")
  void getRelations_returnsDefensiveCopy() {
    FgaRelationModel rel = new FgaRelationModel("doc1", "document", "owner", "user1", "user");
    FgaRelationBatchModel batch = new FgaRelationBatchModel(List.of(rel));

    List<FgaRelationModel> copy = batch.getRelations();
    copy.clear();

    assertThat(batch.getRelations()).hasSize(1);
  }
}
