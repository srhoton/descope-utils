package com.descope.utils;

import com.descope.utils.cli.DescopeUtilsCommand;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

/**
 * Main entry point for the Descope CLI utilities application.
 *
 * <p>This class bootstraps Quarkus in Command Mode and executes the Picocli-based CLI commands with
 * full CDI support.
 */
@QuarkusMain
@TopCommand
public class Main implements QuarkusApplication {

  @Inject CommandLine.IFactory factory;

  @Override
  public int run(String... args) throws Exception {
    return new CommandLine(new DescopeUtilsCommand(), factory).execute(args);
  }
}
