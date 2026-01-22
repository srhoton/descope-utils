package com.descope.utils.config;

/** Enumeration of credential source types for tracking configuration origin. */
public enum CredentialSource {
  /** Credentials loaded from command-line arguments. */
  COMMAND_LINE,

  /** Credentials loaded from environment variables. */
  ENVIRONMENT,

  /** Credentials loaded from files. */
  FILE
}
