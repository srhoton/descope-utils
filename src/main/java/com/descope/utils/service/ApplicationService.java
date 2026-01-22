package com.descope.utils.service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.exception.DescopeException;
import com.descope.model.inbound.InboundApp;
import com.descope.model.inbound.InboundAppCreateResponse;
import com.descope.model.inbound.InboundAppRequest;
import com.descope.sdk.mgmt.InboundAppsService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.Application;
import com.descope.utils.model.OperationResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing Descope applications.
 *
 * <p>Provides operations to create and check existence of Descope applications with idempotency
 * support.
 */
@ApplicationScoped
public class ApplicationService {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

  private final DescopeService descopeService;

  /**
   * Creates a new ApplicationService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public ApplicationService(DescopeService descopeService) {
    this.descopeService = descopeService;
  }

  /**
   * Creates a new Descope application with idempotency support.
   *
   * <p>If an application with the same name already exists, returns the existing application
   * instead of creating a duplicate.
   *
   * @param config The Descope configuration
   * @param name The application name
   * @param description Optional description for the application
   * @return OperationResult containing the created or existing application
   */
  public OperationResult<Application> createApplication(
      DescopeConfig config, String name, String description) {
    logger.info("Creating application: {}", name);

    try {
      DescopeClient client = descopeService.createClient(config);
      InboundAppsService appsService = client.getManagementServices().getInboundAppsService();

      // Check if an application with the same name already exists
      InboundApp[] existingAppsArray = appsService.loadAllApplications();
      List<InboundApp> existingApps = Arrays.asList(existingAppsArray);
      for (InboundApp app : existingApps) {
        if (app.getName().equals(name)) {
          logger.info("Application '{}' already exists (ID: {})", name, app.getId());
          Application existing =
              new Application(
                  app.getId(),
                  app.getName(),
                  app.getDescription() != null ? app.getDescription() : "",
                  Instant.now());
          return OperationResult.alreadyExists(
              existing, "Application '" + name + "' already exists");
        }
      }

      // Create new application
      InboundAppCreateResponse response =
          appsService.createApplication(
              InboundAppRequest.builder()
                  .name(name)
                  .description(description != null ? description : "")
                  .build());

      Application application =
          new Application(
              response.getId(), name, description != null ? description : "", Instant.now());

      logger.info("Successfully created application: {} (ID: {})", name, application.getId());
      return OperationResult.created(
          application, "Application '" + name + "' created successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("create application '" + name + "'", e);
    }
  }
}
