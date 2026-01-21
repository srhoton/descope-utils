package com.descope.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class BuildVerificationTest {

  @Test
  @DisplayName("getGreeting - valid invocation - should return greeting message")
  void getGreeting_validInvocation_shouldReturnGreeting() {
    // Arrange
    BuildVerification verification = new BuildVerification();

    // Act
    String result = verification.getGreeting();

    // Assert
    assertThat(result).isNotNull().isEqualTo("Build configuration successful!");
  }
}
