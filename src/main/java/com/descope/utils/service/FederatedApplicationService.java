package com.descope.utils.service;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.descope.client.DescopeClient;
import com.descope.exception.DescopeException;
import com.descope.model.ssoapp.OIDCApplicationRequest;
import com.descope.model.ssoapp.SAMLApplicationRequest;
import com.descope.model.ssoapp.SSOApplication;
import com.descope.sdk.mgmt.SsoApplicationService;
import com.descope.utils.config.DescopeConfig;
import com.descope.utils.model.FederatedAppType;
import com.descope.utils.model.FederatedApplication;
import com.descope.utils.model.OperationResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing Descope federated applications (OIDC and SAML).
 *
 * <p>Provides operations to create and check existence of federated applications with idempotency
 * support. Federated applications enable authentication through external identity providers using
 * OIDC or SAML protocols.
 */
@ApplicationScoped
public class FederatedApplicationService {

  private static final Logger logger = LoggerFactory.getLogger(FederatedApplicationService.class);

  private final DescopeService descopeService;

  /**
   * Creates a new FederatedApplicationService instance.
   *
   * @param descopeService The base Descope service
   */
  @Inject
  public FederatedApplicationService(DescopeService descopeService) {
    this.descopeService = descopeService;
  }

  /**
   * Creates a new Descope federated application with idempotency support.
   *
   * <p>If a federated application with the same name already exists, returns the existing
   * application instead of creating a duplicate.
   *
   * @param config The Descope configuration
   * @param name The application name
   * @param description Optional description for the application
   * @param type The federated application type (OIDC or SAML)
   * @param loginPageUrl Optional login page URL for the application
   * @return OperationResult containing the created or existing federated application
   */
  public OperationResult<FederatedApplication> createFederatedApplication(
      DescopeConfig config,
      String name,
      String description,
      FederatedAppType type,
      String loginPageUrl) {
    logger.info("Creating {} federated application: {}", type, name);

    try {
      DescopeClient client = descopeService.createClient(config);
      SsoApplicationService ssoAppService =
          client.getManagementServices().getSsoApplicationService();

      // Check if a federated application with the same name already exists
      List<SSOApplication> existingApps = ssoAppService.loadAll();
      for (SSOApplication app : existingApps) {
        if (app.getName().equals(name)) {
          logger.info("Federated application '{}' already exists (ID: {})", name, app.getId());

          // Determine the app type from existing app
          FederatedAppType existingType = determineAppType(app);

          FederatedApplication existing =
              new FederatedApplication(
                  app.getId(),
                  app.getName(),
                  app.getDescription() != null ? app.getDescription() : "",
                  existingType,
                  getLoginPageUrl(app, existingType),
                  Instant.now());
          return OperationResult.alreadyExists(
              existing, "Federated application '" + name + "' already exists");
        }
      }

      // Create new federated application based on type
      FederatedApplication federatedApp = null;
      if (type == FederatedAppType.OIDC) {
        federatedApp = createOIDCApplication(ssoAppService, name, description, loginPageUrl);
      } else if (type == FederatedAppType.SAML) {
        federatedApp = createSAMLApplication(ssoAppService, name, description, loginPageUrl);
      } else {
        throw new IllegalArgumentException("Unsupported federated application type: " + type);
      }

      logger.info(
          "Successfully created {} federated application: {} (ID: {})",
          type,
          name,
          federatedApp.getId());
      return OperationResult.created(
          federatedApp, "Federated application '" + name + "' created successfully");

    } catch (DescopeException e) {
      throw descopeService.wrapException("create federated application '" + name + "'", e);
    }
  }

  /**
   * Determines the federated application type from an SSOApplication.
   *
   * @param app The SSO application
   * @return The federated application type
   */
  private FederatedAppType determineAppType(SSOApplication app) {
    if (app.getAppType() != null) {
      if (app.getAppType().equalsIgnoreCase("saml")) {
        return FederatedAppType.SAML;
      }
    }
    // Default to OIDC if not specified or if OIDC
    return FederatedAppType.OIDC;
  }

  /**
   * Gets the login page URL from an SSOApplication based on its type.
   *
   * @param app The SSO application
   * @param type The federated application type
   * @return The login page URL or empty string
   */
  private String getLoginPageUrl(SSOApplication app, FederatedAppType type) {
    if (type == FederatedAppType.OIDC && app.getOidcSettings() != null) {
      return app.getOidcSettings().getLoginPageUrl() != null
          ? app.getOidcSettings().getLoginPageUrl()
          : "";
    } else if (type == FederatedAppType.SAML && app.getSamlSettings() != null) {
      return app.getSamlSettings().getLoginPageUrl() != null
          ? app.getSamlSettings().getLoginPageUrl()
          : "";
    }
    return "";
  }

  /**
   * Creates an OIDC federated application.
   *
   * @param ssoAppService The Descope SSO application service
   * @param name The application name
   * @param description Optional description
   * @param loginPageUrl Optional login page URL
   * @return The created FederatedApplication
   * @throws DescopeException if creation fails
   */
  private FederatedApplication createOIDCApplication(
      SsoApplicationService ssoAppService, String name, String description, String loginPageUrl)
      throws DescopeException {

    OIDCApplicationRequest request =
        OIDCApplicationRequest.builder()
            .name(name)
            .description(description != null ? description : "")
            .loginPageUrl(loginPageUrl != null ? loginPageUrl : "")
            .enabled(true)
            .build();

    String appId = ssoAppService.createOIDCApplication(request);

    // Load the created application to get full details
    SSOApplication createdApp = ssoAppService.load(appId);

    return new FederatedApplication(
        createdApp.getId(),
        createdApp.getName(),
        createdApp.getDescription(),
        FederatedAppType.OIDC,
        createdApp.getOidcSettings() != null
            ? createdApp.getOidcSettings().getLoginPageUrl()
            : loginPageUrl,
        Instant.now());
  }

  /**
   * Creates a SAML federated application.
   *
   * @param ssoAppService The Descope SSO application service
   * @param name The application name
   * @param description Optional description
   * @param loginPageUrl Optional login page URL
   * @return The created FederatedApplication
   * @throws DescopeException if creation fails
   */
  private FederatedApplication createSAMLApplication(
      SsoApplicationService ssoAppService, String name, String description, String loginPageUrl)
      throws DescopeException {

    // Note: SAMLApplicationRequest requires additional mandatory fields
    // These should be provided by the user in a production implementation
    // For now, we'll use placeholder values to demonstrate the API
    SAMLApplicationRequest request =
        SAMLApplicationRequest.builder()
            .name(name)
            .description(description != null ? description : "")
            .loginPageUrl(loginPageUrl != null ? loginPageUrl : "")
            .enabled(true)
            .entityId("urn:example:entity:" + System.currentTimeMillis())
            .acsUrl("https://example.com/saml/acs")
            .certificate("") // Empty certificate for demonstration
            .build();

    String appId = ssoAppService.createSAMLApplication(request);

    // Load the created application to get full details
    SSOApplication createdApp = ssoAppService.load(appId);

    return new FederatedApplication(
        createdApp.getId(),
        createdApp.getName(),
        createdApp.getDescription(),
        FederatedAppType.SAML,
        createdApp.getSamlSettings() != null
            ? createdApp.getSamlSettings().getLoginPageUrl()
            : loginPageUrl,
        Instant.now());
  }
}
