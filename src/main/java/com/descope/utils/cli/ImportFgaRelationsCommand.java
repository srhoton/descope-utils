package com.descope.utils.cli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.utils.config.ConfigurationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.OperationResult;
import com.descope.utils.model.fga.FgaRelationModel;
import com.descope.utils.service.FgaService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Command to bulk import FGA relation tuples from an NDJSON file.
 *
 * <p>Reads FGA relations from a newline-delimited JSON (NDJSON) file and creates them in batches.
 * Each line must be a JSON object matching FgaRelationModel:
 * {"resource":"doc1","resourceType":"document","relation":"owner","target":"user1","targetType":"user"}
 */
@Command(
    name = "import-fga-relations",
    description = "Bulk import FGA relation tuples from an NDJSON file",
    mixinStandardHelpOptions = true)
public class ImportFgaRelationsCommand implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(ImportFgaRelationsCommand.class);
  private static final int DEFAULT_BATCH_SIZE = 50;

  @Mixin private GlobalOptions globalOptions;

  @Option(
      names = {"-f", "--file"},
      required = true,
      description =
          "Path to NDJSON file containing FGA relation tuples"
              + " (one JSON object per line, fields: resource, resourceType, relation,"
              + " target, targetType)")
  private String file;

  @Option(
      names = {"-b", "--batch-size"},
      description = "Number of relations to create per API call (default: 50)")
  private int batchSize = DEFAULT_BATCH_SIZE;

  @Inject private ConfigurationService configService;
  @Inject private FgaService fgaService;

  @Override
  public void run() {
    try {
      DescopeConfig config =
          configService.loadConfiguration(
              globalOptions.getProjectId(), globalOptions.getManagementKey());

      ObjectMapper mapper =
          new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      List<FgaRelationModel> allRelations = new ArrayList<>();
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        int lineNum = 0;
        while ((line = reader.readLine()) != null) {
          lineNum++;
          line = line.trim();
          if (line.isEmpty()) {
            continue;
          }
          try {
            FgaRelationModel tuple = mapper.readValue(line, FgaRelationModel.class);
            allRelations.add(tuple);
          } catch (IOException e) {
            logger.error("Failed to parse line {}: {}", lineNum, e.getMessage());
            System.err.println("Error parsing line " + lineNum + ": " + e.getMessage());
            System.exit(1);
            return;
          }
        }
      }

      logger.info("Loaded {} FGA relations from file: {}", allRelations.size(), file);
      System.out.println("Loaded " + allRelations.size() + " FGA relations from " + file);

      int totalCreated = 0;
      int totalBatches = (allRelations.size() + batchSize - 1) / batchSize;

      for (int i = 0; i < allRelations.size(); i += batchSize) {
        int end = Math.min(i + batchSize, allRelations.size());
        List<FgaRelationModel> batch = allRelations.subList(i, end);
        int batchNum = (i / batchSize) + 1;

        logger.info("Processing batch {}/{} ({} relations)", batchNum, totalBatches, batch.size());
        System.out.printf(
            "Processing batch %d/%d (%d relations)...%n", batchNum, totalBatches, batch.size());

        OperationResult<List<FgaRelationModel>> result = fgaService.createRelations(config, batch);

        if (!result.isSuccess()) {
          logger.error("Batch {} failed: {}", batchNum, result.getMessage());
          System.err.println("Batch " + batchNum + " failed: " + result.getMessage());
          System.err.println("Successfully created " + totalCreated + " relations before failure");
          System.exit(1);
          return;
        }

        totalCreated += batch.size();
      }

      System.out.println(
          "\nSuccessfully imported "
              + totalCreated
              + " FGA relations in "
              + totalBatches
              + " batches");
      System.exit(0);

    } catch (Exception e) {
      logger.error("Failed to import FGA relations", e);
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
