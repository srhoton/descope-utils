package com.descope.utils.model.fga;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaCheckResultModel. */
class FgaCheckResultModelTest {

  @Test
  @DisplayName("constructor - allowed result - stores correct values")
  void constructor_allowedResult_storesCorrectValues() {
    FgaCheckResultModel result =
        new FgaCheckResultModel(true, "doc1", "document", "owner", "user1", "user", true);

    assertThat(result.isAllowed()).isTrue();
    assertThat(result.getResource()).isEqualTo("doc1");
    assertThat(result.getResourceType()).isEqualTo("document");
    assertThat(result.getRelation()).isEqualTo("owner");
    assertThat(result.getTarget()).isEqualTo("user1");
    assertThat(result.getTargetType()).isEqualTo("user");
    assertThat(result.isDirect()).isTrue();
  }

  @Test
  @DisplayName("constructor - denied result - isAllowed returns false")
  void constructor_deniedResult_isAllowedReturnsFalse() {
    FgaCheckResultModel result =
        new FgaCheckResultModel(false, "doc1", "document", "owner", "user2", "user", false);

    assertThat(result.isAllowed()).isFalse();
    assertThat(result.isDirect()).isFalse();
  }

  @Test
  @DisplayName("equals - same values - returns true")
  void equals_sameValues_returnsTrue() {
    FgaCheckResultModel a =
        new FgaCheckResultModel(true, "doc1", "document", "owner", "user1", "user", true);
    FgaCheckResultModel b =
        new FgaCheckResultModel(true, "doc1", "document", "owner", "user1", "user", true);

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  @DisplayName("toString - returns string with all fields")
  void toString_returnsStringWithAllFields() {
    FgaCheckResultModel result =
        new FgaCheckResultModel(true, "doc1", "document", "owner", "user1", "user", false);

    assertThat(result.toString()).contains("doc1", "document", "owner", "user1", "user", "true");
  }
}
