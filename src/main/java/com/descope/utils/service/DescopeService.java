package com.descope.utils.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.Config;
import com.descope.client.DescopeClient;
import com.descope.utils.config.DescopeConfig;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Base service for Descope SDK operations.
 *
 * <p>Provides the initialized Descope client and common error handling utilities for all Descope
 * service operations.
 */
@ApplicationScoped
public class DescopeService {

  private static final Logger logger = LoggerFactory.getLogger(DescopeService.class);

  /**
   * Creates a Descope client instance from configuration.
   *
   * <p>NOTE: This creates a new client each time. In production, consider caching or using a
   * singleton pattern.
   *
   * @param config The Descope configuration containing credentials
   * @return A configured DescopeClient instance
   */
  public DescopeClient createClient(DescopeConfig config) {
    logger.debug("Creating Descope client for project: {}", config.getProjectId());
    Config sdkConfig =
        Config.builder()
            .projectId(config.getProjectId())
            .managementKey(config.getManagementKey())
            .build();
    return new DescopeClient(sdkConfig);
  }

  /**
   * Wraps SDK exceptions with descriptive error messages.
   *
   * @param operation Description of the operation being performed
   * @param cause The underlying exception
   * @return A RuntimeException with context
   */
  public RuntimeException wrapException(String operation, Exception cause) {
    logger.error("Failed to {}: {}", operation, cause.getMessage(), cause);
    return new RuntimeException("Failed to " + operation + ": " + cause.getMessage(), cause);
  }
}
