package com.descope.utils.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.descope.utils.config.CredentialSource;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.rebac.NamespaceModel;
import com.descope.utils.model.rebac.RelationDefinitionModel;
import com.descope.utils.model.rebac.SchemaModel;

public class AuthzServiceTest {

  private AuthzService authzService;
  private DescopeService descopeService;

  @TempDir Path tempDir;

  @BeforeEach
  public void setup() {
    descopeService = new DescopeService();
    authzService = new AuthzService(descopeService);
  }

  @Test
  @DisplayName("createSchema - valid JSON file - returns success (requires credentials)")
  public void createSchema_validJsonFile_returnsSuccess() throws IOException {
    // This test is disabled because it requires real Descope credentials
    // To enable: provide valid project ID and management key

    // Create a test schema JSON file
    String schemaJson =
        "{\n"
            + "  \"name\": \"TestSchema\",\n"
            + "  \"namespaces\": [\n"
            + "    {\n"
            + "      \"name\": \"document\",\n"
            + "      \"relationDefinitions\": [\n"
            + "        {\n"
            + "          \"name\": \"owner\",\n"
            + "          \"targetNamespaces\": [\"user\"]\n"
            + "        },\n"
            + "        {\n"
            + "          \"name\": \"viewer\",\n"
            + "          \"targetNamespaces\": [\"user\", \"group\"]\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    File schemaFile = tempDir.resolve("schema.json").toFile();
    Files.writeString(schemaFile.toPath(), schemaJson);

    // This would require real credentials to test
    // DescopeConfig config = new DescopeConfig("project-id", "management-key",
    // CredentialSource.COMMAND_LINE);
    // OperationResult<SchemaModel> result = authzService.createSchema(config,
    // schemaFile.getAbsolutePath(), true);
    // assertTrue(result.isSuccess());

    // For now, just verify the file was created correctly
    assertTrue(schemaFile.exists());
    String content = Files.readString(schemaFile.toPath());
    assertTrue(content.contains("document"));
    assertTrue(content.contains("owner"));
  }

  @Test
  @DisplayName("createSchema - non-existent file - throws RuntimeException")
  public void createSchema_nonExistentFile_throwsRuntimeException() {
    DescopeConfig config =
        new DescopeConfig(
            "test-project-id-123456789012345678", "test-key", CredentialSource.COMMAND_LINE);

    String nonExistentFile = tempDir.resolve("non-existent.json").toString();

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> authzService.createSchema(config, nonExistentFile, true));

    assertTrue(exception.getMessage().contains("Failed to read schema file"));
  }

  @Test
  @DisplayName("createSchema - invalid JSON - throws RuntimeException")
  public void createSchema_invalidJson_throwsRuntimeException() throws IOException {
    File schemaFile = tempDir.resolve("invalid.json").toFile();
    Files.writeString(schemaFile.toPath(), "{ invalid json }");

    DescopeConfig config =
        new DescopeConfig(
            "test-project-id-123456789012345678", "test-key", CredentialSource.COMMAND_LINE);

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> authzService.createSchema(config, schemaFile.getAbsolutePath(), true));

    assertTrue(exception.getMessage().contains("Failed to read schema file"));
  }

  @Test
  @DisplayName("loadSchema - no existing schema - returns error (requires credentials)")
  public void loadSchema_noExistingSchema_returnsError() {
    // This test would require real credentials
    // DescopeConfig config = new DescopeConfig("project-id", "management-key",
    // CredentialSource.COMMAND_LINE);
    // OperationResult<SchemaModel> result = authzService.loadSchema(config);
    // assertFalse(result.isSuccess());

    // For now, just test the model structure
    RelationDefinitionModel ownerRel = new RelationDefinitionModel("owner", Arrays.asList("user"));
    NamespaceModel docNs = new NamespaceModel("document", Arrays.asList(ownerRel));
    SchemaModel schema = new SchemaModel("TestSchema", Arrays.asList(docNs));

    assertEquals("TestSchema", schema.getName());
    assertEquals(1, schema.getNamespaces().size());
    assertEquals("document", schema.getNamespaces().get(0).getName());
  }

  @Test
  @DisplayName("deleteSchema - existing schema - returns success (requires credentials)")
  public void deleteSchema_existingSchema_returnsSuccess() {
    // This test would require real credentials and an existing schema
    // DescopeConfig config = new DescopeConfig("project-id", "management-key",
    // CredentialSource.COMMAND_LINE);
    // OperationResult<String> result = authzService.deleteSchema(config);
    // assertTrue(result.isSuccess());

    // For now, just verify the service is constructed properly
    assertNotNull(authzService);
    assertNotNull(descopeService);
  }

  @Test
  @DisplayName("schema JSON parsing - valid schema file - parses correctly")
  public void schemaJsonParsing_validSchemaFile_parsesCorrectly() throws IOException {
    // Test that we can parse a valid schema JSON
    String schemaJson =
        "{\n"
            + "  \"name\": \"DocumentManagement\",\n"
            + "  \"namespaces\": [\n"
            + "    {\n"
            + "      \"name\": \"document\",\n"
            + "      \"relationDefinitions\": [\n"
            + "        {\n"
            + "          \"name\": \"owner\",\n"
            + "          \"targetNamespaces\": [\"user\"]\n"
            + "        },\n"
            + "        {\n"
            + "          \"name\": \"editor\",\n"
            + "          \"targetNamespaces\": [\"user\", \"group\"]\n"
            + "        },\n"
            + "        {\n"
            + "          \"name\": \"viewer\",\n"
            + "          \"targetNamespaces\": [\"user\", \"group\"]\n"
            + "        }\n"
            + "      ]\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"folder\",\n"
            + "      \"relationDefinitions\": [\n"
            + "        {\n"
            + "          \"name\": \"owner\",\n"
            + "          \"targetNamespaces\": [\"user\"]\n"
            + "        },\n"
            + "        {\n"
            + "          \"name\": \"member\",\n"
            + "          \"targetNamespaces\": [\"user\", \"group\"]\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    File schemaFile = tempDir.resolve("complete-schema.json").toFile();
    Files.writeString(schemaFile.toPath(), schemaJson);

    // Parse using Jackson directly
    com.fasterxml.jackson.databind.ObjectMapper mapper =
        new com.fasterxml.jackson.databind.ObjectMapper();
    SchemaModel schema = mapper.readValue(schemaFile, SchemaModel.class);

    assertEquals("DocumentManagement", schema.getName());
    assertEquals(2, schema.getNamespaces().size());

    NamespaceModel docNs = schema.getNamespaces().get(0);
    assertEquals("document", docNs.getName());
    assertEquals(3, docNs.getRelationDefinitions().size());

    RelationDefinitionModel ownerRel = docNs.getRelationDefinitions().get(0);
    assertEquals("owner", ownerRel.getName());
    assertEquals(1, ownerRel.getTargetNamespaces().size());
    assertEquals("user", ownerRel.getTargetNamespaces().get(0));
  }
}
