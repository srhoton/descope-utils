package com.descope.utils.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.descope.utils.config.CredentialSource;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.fga.FgaRelationModel;
import com.descope.utils.model.fga.FgaResourceDetailsModel;
import com.descope.utils.model.fga.FgaResourceIdentifierModel;

/** Unit tests for FgaService. */
public class FgaServiceTest {

  private FgaService fgaService;
  private DescopeConfig invalidConfig;

  @TempDir Path tempDir;

  @BeforeEach
  public void setup() {
    fgaService = new FgaService(new DescopeService());
    invalidConfig =
        new DescopeConfig(
            "Pinvalid000000000000000000", "invalid-key", CredentialSource.COMMAND_LINE);
  }

  @Test
  @DisplayName("saveSchema - invalid credentials - throws RuntimeException")
  public void saveSchema_invalidCredentials_throwsRuntimeException() {
    assertThatThrownBy(() -> fgaService.saveSchema(invalidConfig, "model AuthZ 1.0\ntype user"))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("loadSchema - invalid credentials - throws RuntimeException")
  public void loadSchema_invalidCredentials_throwsRuntimeException() {
    assertThatThrownBy(() -> fgaService.loadSchema(invalidConfig))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("createRelations - invalid credentials - throws RuntimeException")
  public void createRelations_invalidCredentials_throwsRuntimeException() {
    List<FgaRelationModel> relations =
        List.of(new FgaRelationModel("doc1", "document", "owner", "user1", "user"));

    assertThatThrownBy(() -> fgaService.createRelations(invalidConfig, relations))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("deleteRelations - invalid credentials - throws RuntimeException")
  public void deleteRelations_invalidCredentials_throwsRuntimeException() {
    List<FgaRelationModel> relations =
        List.of(new FgaRelationModel("doc1", "document", "owner", "user1", "user"));

    assertThatThrownBy(() -> fgaService.deleteRelations(invalidConfig, relations))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("checkRelations - invalid credentials - throws RuntimeException")
  public void checkRelations_invalidCredentials_throwsRuntimeException() {
    List<FgaRelationModel> relations =
        List.of(new FgaRelationModel("doc1", "document", "owner", "user1", "user"));

    assertThatThrownBy(() -> fgaService.checkRelations(invalidConfig, relations))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("loadResourceDetails - invalid credentials - throws RuntimeException")
  public void loadResourceDetails_invalidCredentials_throwsRuntimeException() {
    List<FgaResourceIdentifierModel> ids =
        List.of(new FgaResourceIdentifierModel("doc1", "document"));

    assertThatThrownBy(() -> fgaService.loadResourceDetails(invalidConfig, ids))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("saveResourceDetails - invalid credentials - throws RuntimeException")
  public void saveResourceDetails_invalidCredentials_throwsRuntimeException() {
    List<FgaResourceDetailsModel> details =
        List.of(new FgaResourceDetailsModel("doc1", "document", "My Document"));

    assertThatThrownBy(() -> fgaService.saveResourceDetails(invalidConfig, details))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("createRelations - multiple relations in list are preserved in model")
  public void createRelations_multipleRelationsInListArePreserved() {
    List<FgaRelationModel> relations =
        List.of(
            new FgaRelationModel("doc1", "document", "owner", "user1", "user"),
            new FgaRelationModel("doc1", "document", "viewer", "user2", "user"),
            new FgaRelationModel("folder1", "folder", "member", "org1", "organization"));

    assertThat(relations).hasSize(3);
    assertThat(relations.get(0).getRelation()).isEqualTo("owner");
    assertThat(relations.get(1).getRelation()).isEqualTo("viewer");
    assertThat(relations.get(2).getResourceType()).isEqualTo("folder");
  }

  @Test
  @DisplayName("saveSchema - can read DSL from temp file and DSL is valid string")
  public void saveSchema_canReadDslFromTempFile() throws IOException {
    String dsl = "model AuthZ 1.0\ntype user\ntype document\n  relation owner: user";
    File schemaFile = tempDir.resolve("schema.authz").toFile();
    Files.writeString(schemaFile.toPath(), dsl);

    assertTrue(schemaFile.exists());
    String readBack = Files.readString(schemaFile.toPath());
    assertThat(readBack).isEqualTo(dsl);
  }
}
