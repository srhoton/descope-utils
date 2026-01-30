# Descope Management CLI Utilities

A command-line interface (CLI) tool for programmatically managing Descope resources including applications, tenants, users, authorization schemas, and RBAC roles. Built with Java, Quarkus, and the official Descope Java SDK.

## Features

- **Application Management**: Create and manage Descope applications and federated applications (OIDC/SAML SSO)
- **Tenant Management**: Create tenants and associate applications with tenants
- **User Management**: Create users, migrate legacy users, and manage custom attributes
- **Authentication**: Headless password authentication (sign-in, sign-up, password management)
- **RBAC Role Management**: Full CRUD operations on roles and user role assignments
- **ReBAC Schema Management**: Create, load, and delete authorization schemas
- **FGA (Fine-Grained Authorization)**: Create, delete, check, and query relation tuples
- **Multiple Configuration Sources**: CLI arguments, environment variables, or files
- **Idempotent Operations**: Safe re-execution without creating duplicates
- **Multiple Output Formats**: JSON and human-readable text

## Prerequisites

- Java 17 or later
- Gradle 8.x (wrapper included)
- Descope project ID and management key

## Installation

### Build from Source

```bash
# Clone the repository
git clone <repository-url>
cd descope-utils

# Build the project
./gradlew build

# Run tests
./gradlew test

# Check code coverage
./gradlew jacocoTestReport
```

### Create Native Executable (Optional)

```bash
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

## Configuration

The CLI requires two credentials to interact with the Descope API:
- Project ID
- Management Key

These can be provided in three ways (in order of precedence):

### 1. Command-Line Arguments (Highest Priority)

```bash
java -jar build/quarkus-app/quarkus-run.jar create-app \
  --project-id=YOUR_PROJECT_ID \
  --management-key=YOUR_MANAGEMENT_KEY \
  my-application
```

### 2. Environment Variables

```bash
export DESCOPE_PROJECT_ID=YOUR_PROJECT_ID
export DESCOPE_MANAGEMENT_KEY=YOUR_MANAGEMENT_KEY

java -jar build/quarkus-app/quarkus-run.jar create-app my-application
```

### 3. Configuration Files (Lowest Priority)

Create files containing your credentials:

```bash
# Project ID file
echo "YOUR_PROJECT_ID" > ~/descope/project_id

# Management Key file
echo "YOUR_MANAGEMENT_KEY" > ~/descope/management_key
```

Then configure file paths:

```bash
export DESCOPE_PROJECT_ID_FILE=~/descope/project_id
export DESCOPE_MANAGEMENT_KEY_FILE=~/descope/management_key
```

Or use default file paths:
- `~/git/tmp/descope/project_id`
- `~/git/tmp/descope/management_key`

## Usage

### Global Options

All commands support the following global options:

```bash
Options:
  -p, --project-id=<projectId>        Descope project ID
  -k, --management-key=<managementKey> Descope management key
  -o, --output=<format>               Output format: TEXT or JSON (default: TEXT)
  -h, --help                          Show help message
```

---

## Application Commands

### create-app

Create a new Descope application (OIDC/SAML inbound app).

```bash
# Basic usage
java -jar build/quarkus-app/quarkus-run.jar create-app my-application

# With description
java -jar build/quarkus-app/quarkus-run.jar create-app \
  --description="My application description" \
  my-application

# JSON output
java -jar build/quarkus-app/quarkus-run.jar create-app \
  --output=JSON \
  my-application
```

**Parameters:**
- `name` (required): Application name
- `--description`: Optional application description

### create-federated-app

Create a new Descope federated application for SSO integration using OIDC or SAML protocols.

```bash
# Create OIDC federated application (default)
java -jar build/quarkus-app/quarkus-run.jar create-federated-app my-sso-app

# Create SAML federated application
java -jar build/quarkus-app/quarkus-run.jar create-federated-app \
  --type=saml \
  my-saml-app

# With description and login page URL
java -jar build/quarkus-app/quarkus-run.jar create-federated-app \
  --type=oidc \
  --description="My company SSO integration" \
  --login-page-url="https://mycompany.com/login" \
  my-sso-app
```

**Parameters:**
- `name` (required): Federated application name
- `--type`: Application type - `oidc` or `saml` (default: `oidc`)
- `--description`: Optional application description
- `--login-page-url`: Optional login page URL for the application

---

## Tenant Commands

### create-tenant

Create a new Descope tenant and optionally associate it with an application.

```bash
# Basic usage
java -jar build/quarkus-app/quarkus-run.jar create-tenant my-tenant

# Associate with application
java -jar build/quarkus-app/quarkus-run.jar create-tenant \
  --app-id=app-12345678 \
  my-tenant
```

**Parameters:**
- `name` (required): Tenant name
- `--app-id`: Optional application ID to associate with

### add-app-to-tenant

Associate an application with a tenant.

```bash
# Add a federated app to a tenant
java -jar build/quarkus-app/quarkus-run.jar add-app-to-tenant \
  --tenant-id=tenant-87654321 \
  --app-id=ssoapp-12345678

# Using short options
java -jar build/quarkus-app/quarkus-run.jar add-app-to-tenant \
  -t tenant-87654321 \
  -a ssoapp-12345678
```

**Parameters:**
- `--tenant-id` or `-t` (required): Tenant ID
- `--app-id` or `-a` (required): Application ID

---

## User Commands

### create-user

Create a new user in a Descope tenant.

```bash
# Basic usage
java -jar build/quarkus-app/quarkus-run.jar create-user \
  --tenant-id=tenant-87654321 \
  user@example.com

# With email
java -jar build/quarkus-app/quarkus-run.jar create-user \
  --tenant-id=tenant-87654321 \
  --email=user@example.com \
  user@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--tenant-id` (required): Tenant ID the user belongs to
- `--email`: Optional user email address

### migrate-legacy-user

Migrate a legacy user to Descope with their existing bcrypt password hash preserved.

```bash
# Migrate a user with bcrypt password hash
java -jar build/quarkus-app/quarkus-run.jar migrate-legacy-user \
  --tenant-id=tenant-87654321 \
  --first-name=John \
  --last-name=Doe \
  --email=john.doe@example.com \
  --bcrypt-hash='$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.SjRTvllzLR' \
  --roles=user,admin \
  john.doe@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--tenant-id` or `-t` (required): Tenant ID
- `--first-name` (required): User's first name
- `--last-name` (required): User's last name
- `--email` or `-e` (required): User's email address
- `--bcrypt-hash` or `-b` (required): Existing bcrypt password hash
- `--roles` or `-r`: Comma-separated list of roles to assign

### update-user-attribute

Update a custom attribute on an existing user.

```bash
# Set a string attribute
java -jar build/quarkus-app/quarkus-run.jar update-user-attribute \
  --key=department \
  --value=Engineering \
  --type=STRING \
  user@example.com

# Set a number attribute
java -jar build/quarkus-app/quarkus-run.jar update-user-attribute \
  --key=employeeId \
  --value=12345 \
  --type=NUMBER \
  user@example.com

# Set a boolean attribute
java -jar build/quarkus-app/quarkus-run.jar update-user-attribute \
  --key=isManager \
  --value=true \
  --type=BOOLEAN \
  user@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--key` or `-k` (required): Custom attribute key (must exist in Descope console)
- `--value` or `-v` (required): Attribute value
- `--type`: Value type - `STRING`, `NUMBER`, or `BOOLEAN` (default: `STRING`)

---

## Authentication Commands

### authenticate

Authenticate a user with password and get JWT tokens.

```bash
# Basic authentication
java -jar build/quarkus-app/quarkus-run.jar authenticate \
  --password=MySecurePassword123 \
  user@example.com

# JSON output with tokens
java -jar build/quarkus-app/quarkus-run.jar authenticate \
  --password=MySecurePassword123 \
  --output=JSON \
  user@example.com
```

**Parameters:**
- `loginId` (required): User login ID (email, phone, or username)
- `--password` or `-p` (required): User's password

**Output:** Returns session JWT and refresh JWT tokens on success.

### signup

Sign up a new user with password authentication.

```bash
# Basic signup
java -jar build/quarkus-app/quarkus-run.jar signup \
  --password=MySecurePassword123 \
  newuser@example.com

# With email and name
java -jar build/quarkus-app/quarkus-run.jar signup \
  --password=MySecurePassword123 \
  --email=newuser@example.com \
  --name="John Doe" \
  newuser@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--password` or `-p` (required): Password for the new user
- `--email` or `-e`: Optional email address
- `--name` or `-n`: Optional display name

### set-password

Set a password for an existing user.

```bash
# Set as active password (user can login immediately)
java -jar build/quarkus-app/quarkus-run.jar set-password \
  --password=NewPassword123 \
  user@example.com

# Set as temporary password (user must change on next login)
java -jar build/quarkus-app/quarkus-run.jar set-password \
  --password=TempPassword123 \
  --temporary \
  user@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--password` or `-p` (required): Password to set
- `--temporary` or `-t`: Set as temporary password (default: false)

---

## RBAC Role Commands

### create-role

Create a new RBAC role.

```bash
# Create a project-level role
java -jar build/quarkus-app/quarkus-run.jar create-role admin

# Create a role with description and permissions
java -jar build/quarkus-app/quarkus-run.jar create-role \
  --description="Administrator role with full access" \
  --permissions=read,write,delete \
  admin

# Create a tenant-specific role
java -jar build/quarkus-app/quarkus-run.jar create-role \
  --tenant=tenant-12345678 \
  --description="Tenant admin role" \
  --permissions=manage_users,manage_settings \
  tenant-admin
```

**Parameters:**
- `name` (required): Role name
- `--description` or `-d`: Optional role description
- `--permissions` or `-p`: Comma-separated list of permission names
- `--tenant` or `-t`: Tenant ID for tenant-specific role (omit for project-level)

### list-roles

List all RBAC roles.

```bash
# List all roles (text output)
java -jar build/quarkus-app/quarkus-run.jar list-roles

# JSON output
java -jar build/quarkus-app/quarkus-run.jar list-roles --output=JSON
```

### update-role

Update an existing RBAC role.

```bash
# Update role description
java -jar build/quarkus-app/quarkus-run.jar update-role \
  --description="Updated description" \
  admin

# Rename a role
java -jar build/quarkus-app/quarkus-run.jar update-role \
  --new-name=super-admin \
  admin

# Update permissions
java -jar build/quarkus-app/quarkus-run.jar update-role \
  --permissions=read,write,delete,admin \
  admin

# Update a tenant-specific role
java -jar build/quarkus-app/quarkus-run.jar update-role \
  --tenant=tenant-12345678 \
  --description="Updated tenant role" \
  tenant-admin
```

**Parameters:**
- `name` (required): Current role name
- `--new-name` or `-n`: New name for the role
- `--description` or `-d`: New description
- `--permissions` or `-p`: New comma-separated list of permissions
- `--tenant` or `-t`: Tenant ID for tenant-specific role

### delete-role

Delete an RBAC role.

```bash
# Delete a project-level role
java -jar build/quarkus-app/quarkus-run.jar delete-role admin

# Delete a tenant-specific role
java -jar build/quarkus-app/quarkus-run.jar delete-role \
  --tenant=tenant-12345678 \
  tenant-admin
```

**Parameters:**
- `name` (required): Role name to delete
- `--tenant` or `-t`: Tenant ID for tenant-specific role

---

## User Role Assignment Commands

### add-user-role

Add roles to a user.

```bash
# Add project-level roles
java -jar build/quarkus-app/quarkus-run.jar add-user-role \
  --roles=admin,editor \
  user@example.com

# Add tenant-specific roles
java -jar build/quarkus-app/quarkus-run.jar add-user-role \
  --roles=tenant-admin,viewer \
  --tenant=tenant-12345678 \
  user@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--roles` or `-r` (required): Comma-separated list of role names to add
- `--tenant` or `-t`: Tenant ID for tenant-specific roles (omit for project-level)

### remove-user-role

Remove roles from a user.

```bash
# Remove project-level roles
java -jar build/quarkus-app/quarkus-run.jar remove-user-role \
  --roles=admin \
  user@example.com

# Remove tenant-specific roles
java -jar build/quarkus-app/quarkus-run.jar remove-user-role \
  --roles=tenant-admin \
  --tenant=tenant-12345678 \
  user@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--roles` or `-r` (required): Comma-separated list of role names to remove
- `--tenant` or `-t`: Tenant ID for tenant-specific roles

### set-user-roles

Set roles for a user (replaces all existing roles).

```bash
# Set project-level roles (replaces all existing)
java -jar build/quarkus-app/quarkus-run.jar set-user-roles \
  --roles=viewer,editor \
  user@example.com

# Set tenant-specific roles (replaces all existing in that tenant)
java -jar build/quarkus-app/quarkus-run.jar set-user-roles \
  --roles=user,contributor \
  --tenant=tenant-12345678 \
  user@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--roles` or `-r` (required): Comma-separated list of role names to set
- `--tenant` or `-t`: Tenant ID for tenant-specific roles

---

## ReBAC Schema Commands

### create-rebac-schema

Create or update a ReBAC authorization schema from a JSON file.

```bash
# Create schema from file
java -jar build/quarkus-app/quarkus-run.jar create-rebac-schema schema.json

# Example schema.json:
# {
#   "name": "my-schema",
#   "namespaces": [
#     {
#       "name": "document",
#       "relationDefinitions": [
#         { "name": "owner" },
#         { "name": "viewer" }
#       ]
#     }
#   ]
# }
```

**Parameters:**
- `schemaFile` (required): Path to JSON file containing the ReBAC schema

### load-rebac-schema

Load and display the current ReBAC authorization schema.

```bash
# Display current schema (text)
java -jar build/quarkus-app/quarkus-run.jar load-rebac-schema

# JSON output
java -jar build/quarkus-app/quarkus-run.jar load-rebac-schema --output=JSON
```

### delete-rebac-schema

Delete the current ReBAC authorization schema.

```bash
java -jar build/quarkus-app/quarkus-run.jar delete-rebac-schema
```

---

## FGA (Fine-Grained Authorization) Commands

### create-fga-relation

Create FGA relation tuple(s) between targets and resources.

```bash
# Create a single relation
java -jar build/quarkus-app/quarkus-run.jar create-fga-relation \
  --resource=document:doc-123 \
  --relation-definition=owner \
  --target=user:user-456

# Create multiple relations (same resource, multiple targets)
java -jar build/quarkus-app/quarkus-run.jar create-fga-relation \
  --resource=document:doc-123 \
  --relation-definition=viewer \
  --target=user:user-789,user:user-012
```

**Parameters:**
- `--resource` or `-r` (required): Resource in format `namespace:id`
- `--relation-definition` or `-d` (required): Relation definition name
- `--target` or `-t` (required): Target(s) in format `namespace:id` (comma-separated for multiple)

### delete-fga-relation

Delete FGA relation tuple(s).

```bash
# Delete a single relation
java -jar build/quarkus-app/quarkus-run.jar delete-fga-relation \
  --resource=document:doc-123 \
  --relation-definition=owner \
  --target=user:user-456

# Delete multiple relations
java -jar build/quarkus-app/quarkus-run.jar delete-fga-relation \
  --resource=document:doc-123 \
  --relation-definition=viewer \
  --target=user:user-789,user:user-012
```

**Parameters:**
- `--resource` or `-r` (required): Resource in format `namespace:id`
- `--relation-definition` or `-d` (required): Relation definition name
- `--target` or `-t` (required): Target(s) in format `namespace:id`

### check-fga-relation

Check if FGA relation tuple(s) exist.

```bash
# Check a single relation
java -jar build/quarkus-app/quarkus-run.jar check-fga-relation \
  --resource=document:doc-123 \
  --relation-definition=owner \
  --target=user:user-456

# Check multiple targets
java -jar build/quarkus-app/quarkus-run.jar check-fga-relation \
  --resource=document:doc-123 \
  --relation-definition=viewer \
  --target=user:user-789,user:user-012
```

**Parameters:**
- `--resource` or `-r` (required): Resource in format `namespace:id`
- `--relation-definition` or `-d` (required): Relation definition name
- `--target` or `-t` (required): Target(s) to check

### query-fga-relations

Query FGA relations with different modes.

```bash
# Query all relations for a resource
java -jar build/quarkus-app/quarkus-run.jar query-fga-relations \
  --mode=resource \
  --resource=document:doc-123

# Query all relations for a target (what can this user access?)
java -jar build/quarkus-app/quarkus-run.jar query-fga-relations \
  --mode=target \
  --target=user:user-456

# Query who has a specific relation to a resource
java -jar build/quarkus-app/quarkus-run.jar query-fga-relations \
  --mode=who \
  --resource=document:doc-123 \
  --relation-definition=owner

# Query what resources a target has a relation to
java -jar build/quarkus-app/quarkus-run.jar query-fga-relations \
  --mode=what \
  --target=user:user-456 \
  --relation-definition=owner
```

**Parameters:**
- `--mode` or `-m` (required): Query mode - `resource`, `target`, `who`, or `what`
- `--resource` or `-r`: Resource in format `namespace:id`
- `--target` or `-t`: Target in format `namespace:id`
- `--relation-definition` or `-d`: Relation definition name

---

## Idempotency

All create operations are idempotent. If a resource with the same name/identifier already exists, the CLI will:
- Return the existing resource
- Set `created: false` in the result
- Display a message indicating the resource already exists

This allows safe re-execution of commands without creating duplicates.

## Error Handling

The CLI provides detailed error messages for common issues:

- **Missing credentials**: Clear message about which credential is missing and how to provide it
- **Invalid credentials**: Validation errors from the Descope API
- **Resource not found**: When referencing non-existent resources
- **Network errors**: Connection issues with the Descope API
- **Validation errors**: Invalid input parameters

Error messages are written to stderr, while successful output goes to stdout.

**Exit Codes:**
- `0`: Success
- `1`: General error

## Development

### Project Structure

```
descope-utils/
├── src/
│   ├── main/
│   │   ├── java/com/descope/utils/
│   │   │   ├── cli/              # CLI commands (Picocli)
│   │   │   ├── config/           # Configuration management
│   │   │   ├── model/            # Domain models
│   │   │   ├── output/           # Output formatters
│   │   │   ├── service/          # Service layer (Descope SDK integration)
│   │   │   └── Main.java         # Application entry point
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/descope/utils/
├── build.gradle                   # Gradle build configuration
├── settings.gradle
└── README.md
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests ApplicationServiceTest

# Run with coverage report
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

### Code Quality

The project uses several tools to ensure code quality:

- **Spotless**: Automatic code formatting (Google Java Format)
- **JaCoCo**: Code coverage reporting (80% minimum)
- **JUnit 5**: Unit testing framework
- **AssertJ**: Fluent assertions
- **Mockito**: Mocking framework

```bash
# Format code
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck

# Run all quality checks
./gradlew check
```

### Adding New Commands

To add a new command:

1. Create a command class in `src/main/java/com/descope/utils/cli/`
2. Implement `Runnable` and use Picocli annotations
3. Add the command to `DescopeUtilsCommand` subcommands
4. Create corresponding tests

Example:

```java
@Command(
    name = "my-command",
    description = "Description of my command",
    mixinStandardHelpOptions = true)
public class MyCommand implements Runnable {

  @Inject MyService myService;
  @Inject ConfigurationService configService;
  @Inject OutputFormatter outputFormatter;

  @Mixin private GlobalOptions globalOptions;

  @Parameters(index = "0", description = "Parameter description")
  private String myParam;

  @Override
  public void run() {
    // Implementation
  }
}
```

## Architecture

### Component Overview

1. **CLI Layer** (`cli` package): Handles command parsing, argument validation, and user interaction using Picocli
2. **Service Layer** (`service` package): Business logic and Descope SDK integration with error handling
3. **Configuration Layer** (`config` package): Multi-source credential loading with precedence rules
4. **Model Layer** (`model` package): Domain objects (Application, Tenant, User, Role, OperationResult)
5. **Output Layer** (`output` package): Formatters for JSON and human-readable text output

### Technology Stack

- **Framework**: Quarkus 3.17.5 (for CDI, fast startup, and native compilation)
- **CLI Framework**: Picocli (for command parsing and help generation)
- **SDK**: Descope Java SDK 1.0.60 (official SDK for Descope API)
- **Build Tool**: Gradle 8.11.1 with Kotlin DSL
- **Testing**: JUnit 5, AssertJ, Mockito
- **Code Quality**: Spotless (Google Java Format), JaCoCo

### Design Principles

- **Dependency Injection**: Uses Quarkus CDI for loose coupling and testability
- **Immutability**: Domain models are immutable for thread safety
- **Single Responsibility**: Each class has a focused, well-defined purpose
- **Command Pattern**: CLI commands follow the command pattern with Picocli
- **Separation of Concerns**: Clear boundaries between layers

## Troubleshooting

### Common Issues

**Issue**: `Invalid project ID - must be over 27 characters long`

**Solution**: Ensure your project ID is valid. Check the Descope console for the correct project ID format.

---

**Issue**: `Authentication failed`

**Solution**: Verify your management key is correct and has the necessary permissions in the Descope console.

---

**Issue**: `Command not found`

**Solution**: Ensure you're running the command from the project directory, or use the full path to the JAR file.

---

**Issue**: `No credentials found`

**Solution**: Provide credentials via command-line arguments, environment variables, or configuration files as described in the Configuration section.

---

**Issue**: `Custom attribute key not found`

**Solution**: Custom attributes must be pre-configured in the Descope console before they can be set via the CLI.

## Contributing

Contributions are welcome! Please follow these guidelines:

1. **Code Style**: Follow Google Java Format (enforced by Spotless)
2. **Testing**: Add tests for all new features (minimum 80% coverage)
3. **Documentation**: Update README and JavaDoc for new features
4. **Commits**: Use conventional commit messages

### Development Workflow

```bash
# 1. Create a feature branch
git checkout -b feat/my-feature

# 2. Make changes and format code
./gradlew spotlessApply

# 3. Run tests
./gradlew test

# 4. Commit changes
git add .
git commit -m "feat: add my feature"

# 5. Push and create PR
git push origin feat/my-feature
```

## License

[Add your license information here]

## Support

For issues, questions, or contributions:
- GitHub Issues: [repository-url]/issues
- Documentation: [documentation-url]
- Descope Support: https://docs.descope.com

## References

- [Descope Java SDK](https://github.com/descope/descope-java)
- [Descope Documentation](https://docs.descope.com)
- [Quarkus Documentation](https://quarkus.io/guides/)
- [Picocli Documentation](https://picocli.info/)
