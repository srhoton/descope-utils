# Descope Management CLI Utilities

A command-line interface (CLI) tool for programmatically managing Descope resources including applications, tenants, and users. Built with Java, Quarkus, and the official Descope Java SDK.

## Features

- Create and manage Descope applications
- Create and manage Descope tenants
- Create and manage users within tenants
- Multiple configuration sources (CLI arguments, environment variables, files)
- Idempotent operations (checks if resources exist before creating)
- Multiple output formats (JSON and human-readable text)
- Comprehensive error handling with detailed messages

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
  --project-id=<projectId>          Descope project ID
  --management-key=<managementKey>  Descope management key
  --format=<format>                 Output format: json or text (default: text)
  -h, --help                        Show help message
  -V, --version                     Show version information
```

### Create Application

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
  --format=json \
  my-application
```

**Parameters:**
- `name` (required): Application name
- `--description`: Optional application description

**Example Output (Text):**

```
✅ SUCCESS: Application 'my-application' created successfully

Application Details:
  ID:          app-12345678
  Name:        my-application
  Description: My application description
  Created:     2026-01-21T10:30:00Z
```

**Example Output (JSON):**

```json
{
  "success": true,
  "created": true,
  "data": {
    "id": "app-12345678",
    "name": "my-application",
    "description": "My application description",
    "createdAt": "2026-01-21T10:30:00Z"
  },
  "message": "Application 'my-application' created successfully"
}
```

### Create Tenant

Create a new Descope tenant and optionally associate it with an application.

```bash
# Basic usage
java -jar build/quarkus-app/quarkus-run.jar create-tenant my-tenant

# Associate with application
java -jar build/quarkus-app/quarkus-run.jar create-tenant \
  --app-id=app-12345678 \
  my-tenant

# JSON output
java -jar build/quarkus-app/quarkus-run.jar create-tenant \
  --format=json \
  --app-id=app-12345678 \
  my-tenant
```

**Parameters:**
- `name` (required): Tenant name
- `--app-id`: Optional application ID to associate with

**Example Output (Text):**

```
✅ SUCCESS: Tenant 'my-tenant' created successfully

Tenant Details:
  ID:          tenant-87654321
  Name:        my-tenant
  App ID:      app-12345678
  Created:     2026-01-21T10:35:00Z
```

### Create User

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

# JSON output
java -jar build/quarkus-app/quarkus-run.jar create-user \
  --format=json \
  --tenant-id=tenant-87654321 \
  --email=user@example.com \
  user@example.com
```

**Parameters:**
- `loginId` (required): User login ID
- `--tenant-id` (required): Tenant ID the user belongs to
- `--email`: Optional user email address

**Example Output (Text):**

```
✅ SUCCESS: User 'user@example.com' created successfully

User Details:
  ID:        user-99887766
  Login ID:  user@example.com
  Email:     user@example.com
  Tenant ID: tenant-87654321
  Created:   2026-01-21T10:40:00Z
```

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
- **Resource not found**: When referencing non-existent resources (e.g., invalid app-id for tenant)
- **Network errors**: Connection issues with the Descope API
- **Validation errors**: Invalid input parameters

Error messages are written to stderr, while successful output goes to stdout.

**Exit Codes:**
- `0`: Success
- `1`: General error
- `2`: Invalid parameters
- `3`: Resource not found
- `4`: Authentication/authorization error

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
│       ├── java/com/descope/utils/
│       │   ├── cli/              # CLI unit tests
│       │   ├── config/           # Configuration tests
│       │   ├── integration/      # Integration tests
│       │   ├── model/            # Model tests
│       │   ├── output/           # Formatter tests
│       │   └── service/          # Service tests
│       └── resources/
│           └── application-test.properties
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
2. Extend the appropriate base class and use Picocli annotations
3. Implement the `call()` method
4. Add the command to `DescopeUtilsCommand` subcommands
5. Create corresponding tests

Example:

```java
@Command(
    name = "create-resource",
    description = "Create a new resource",
    mixinStandardHelpOptions = true)
public class CreateResourceCommand implements Callable<Integer> {

  @Inject
  ResourceService resourceService;

  @Inject
  ConfigurationService configService;

  @Inject
  OutputFormatter outputFormatter;

  @Parameters(index = "0", description = "Resource name")
  private String name;

  @Override
  public Integer call() {
    // Implementation
    return 0;
  }
}
```

## Architecture

### Component Overview

1. **CLI Layer** (`cli` package): Handles command parsing, argument validation, and user interaction using Picocli
2. **Service Layer** (`service` package): Business logic and Descope SDK integration with error handling
3. **Configuration Layer** (`config` package): Multi-source credential loading with precedence rules
4. **Model Layer** (`model` package): Domain objects (Application, Tenant, User, OperationResult)
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
