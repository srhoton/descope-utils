package com.descope.utils.model.fga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FgaSchemaModel. */
class FgaSchemaModelTest {

  private static final String SAMPLE_DSL =
      "model AuthZ 1.0\ntype user\ntype document\n  relation owner: user";

  @Test
  @DisplayName("constructor - valid DSL - creates model with correct value")
  void constructor_validDsl_createsModelWithCorrectValue() {
    FgaSchemaModel model = new FgaSchemaModel(SAMPLE_DSL);

    assertThat(model.getDsl()).isEqualTo(SAMPLE_DSL);
  }

  @Test
  @DisplayName("constructor - null DSL - throws NullPointerException")
  void constructor_nullDsl_throwsNullPointerException() {
    assertThatThrownBy(() -> new FgaSchemaModel(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("DSL");
  }

  @Test
  @DisplayName("equals - same DSL - returns true")
  void equals_sameDsl_returnsTrue() {
    FgaSchemaModel a = new FgaSchemaModel(SAMPLE_DSL);
    FgaSchemaModel b = new FgaSchemaModel(SAMPLE_DSL);

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  @DisplayName("equals - different DSL - returns false")
  void equals_differentDsl_returnsFalse() {
    FgaSchemaModel a = new FgaSchemaModel("model AuthZ 1.0\ntype user");
    FgaSchemaModel b = new FgaSchemaModel("model AuthZ 1.0\ntype document");

    assertThat(a).isNotEqualTo(b);
  }

  @Test
  @DisplayName("toString - contains DSL")
  void toString_containsDsl() {
    FgaSchemaModel model = new FgaSchemaModel(SAMPLE_DSL);

    assertThat(model.toString()).contains(SAMPLE_DSL);
  }
}
