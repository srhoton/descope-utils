# Contributing to Descope Management CLI Utilities

Thank you for your interest in contributing to the Descope Management CLI Utilities! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

- Be respectful and inclusive
- Focus on constructive feedback
- Help create a welcoming environment for all contributors

## Getting Started

### Prerequisites

- Java 17 or later
- Gradle 8.x (wrapper included in the repository)
- Git
- A Descope account with project ID and management key for testing

### Setting Up Development Environment

```bash
# 1. Fork and clone the repository
git clone https://github.com/YOUR_USERNAME/descope-utils.git
cd descope-utils

# 2. Create a feature branch
git checkout -b feat/your-feature-name

# 3. Build the project
./gradlew build

# 4. Run tests to ensure everything works
./gradlew test
```

## Development Workflow

### 1. Code Style

The project follows **Google Java Format** and enforces it with **Spotless**.

```bash
# Automatically format all code
./gradlew spotlessApply

# Check if code is formatted correctly
./gradlew spotlessCheck
```

**Key Code Style Rules:**

- Use Google Java Format style
- Maximum line length: 100 characters
- Use 2-space indentation
- Organize imports: java, javax, org, com, then blank line
- Remove unused imports
- Trim trailing whitespace
- End files with newline

### 2. Naming Conventions

Follow standard Java naming conventions as outlined in the project's Java coding rules:

- `CamelCase` for class names (e.g., `ApplicationService`)
- `camelCase` for method and variable names (e.g., `createApplication`)
- `UPPER_SNAKE_CASE` for constants (e.g., `DEFAULT_TIMEOUT`)
- Descriptive names that clearly indicate purpose
- Avoid abbreviations except for standard ones (HTTP, URL, ID)

### 3. Testing Requirements

All contributions must include appropriate tests with minimum 80% code coverage.

**Test Structure:**

```java
@DisplayName("methodName - scenario - expectedBehavior")
@Test
void methodName_scenario_expectedBehavior() {
    // Arrange
    // ... setup test data

    // Act
    // ... call method under test

    // Assert
    // ... verify results
}
```

**Running Tests:**

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests ApplicationServiceTest

# Run tests with coverage report
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html

# Verify coverage meets threshold (80%)
./gradlew jacocoTestCoverageVerification
```

**Test Types:**

- **Unit Tests**: Test individual classes in isolation using mocks
- **Integration Tests**: Test interactions with real Descope API
- **Component Tests**: Test interactions between multiple classes

### 4. Documentation

All code must be properly documented:

**JavaDoc Requirements:**

```java
/**
 * Creates a new Descope application.
 *
 * <p>This method checks if an application with the same name exists
 * before creating a new one to ensure idempotency.
 *
 * @param name The application name
 * @param description Optional description for the application
 * @return OperationResult containing the created or existing application
 * @throws IllegalArgumentException if name is null or empty
 */
public OperationResult<Application> createApplication(String name, String description) {
    // Implementation
}
```

**Documentation Standards:**

- Add JavaDoc for all public classes, methods, and interfaces
- Include `@param`, `@return`, `@throws` tags where applicable
- Explain the "why" in comments, not just the "what"
- Update README.md for user-facing changes
- Update this CONTRIBUTING.md for process changes

### 5. Commit Messages

Follow conventional commit message format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, missing semi-colons, etc.)
- `refactor`: Code refactoring without behavior changes
- `test`: Adding or updating tests
- `chore`: Maintenance tasks, dependency updates

**Examples:**

```bash
feat(cli): add create-role command

Add new CLI command for creating Descope roles with name and
permissions. Includes unit tests and integration tests.

Closes #123

---

fix(service): handle null response from SDK

The Descope SDK can return null in certain error conditions.
Add null checks and throw appropriate exceptions.

Fixes #456

---

docs(readme): update installation instructions

Add instructions for building native executable and clarify
Java version requirements.
```

### 6. Pull Request Process

1. **Create a Feature Branch**
   ```bash
   git checkout -b feat/your-feature-name
   ```

2. **Make Your Changes**
   - Write code following the style guide
   - Add comprehensive tests
   - Update documentation

3. **Run Quality Checks**
   ```bash
   # Format code
   ./gradlew spotlessApply

   # Run all tests
   ./gradlew test

   # Run all quality checks
   ./gradlew check
   ```

4. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "feat(scope): description"
   ```

5. **Push to Your Fork**
   ```bash
   git push origin feat/your-feature-name
   ```

6. **Create Pull Request**
   - Provide a clear description of the changes
   - Reference any related issues
   - Ensure all CI checks pass
   - Request review from maintainers

**Pull Request Checklist:**

- [ ] Code follows Google Java Format style
- [ ] All tests pass locally
- [ ] New tests added for new features
- [ ] Code coverage >= 80%
- [ ] JavaDoc added for public APIs
- [ ] README updated if needed
- [ ] Commit messages follow conventional format
- [ ] No merge conflicts with main branch

## Project Structure

Understanding the project structure will help you contribute effectively:

```
descope-utils/
├── src/
│   ├── main/
│   │   ├── java/com/descope/utils/
│   │   │   ├── cli/              # Command-line interface (Picocli commands)
│   │   │   │   ├── DescopeUtilsCommand.java  # Main command with subcommands
│   │   │   │   ├── CreateAppCommand.java      # Create application command
│   │   │   │   ├── CreateTenantCommand.java   # Create tenant command
│   │   │   │   ├── CreateUserCommand.java     # Create user command
│   │   │   │   └── GlobalOptions.java         # Shared CLI options
│   │   │   │
│   │   │   ├── config/           # Configuration management
│   │   │   │   ├── ConfigurationService.java  # Multi-source config loading
│   │   │   │   ├── DescopeConfig.java         # Configuration holder
│   │   │   │   └── CredentialSource.java      # Enum for config sources
│   │   │   │
│   │   │   ├── model/            # Domain models
│   │   │   │   ├── Application.java           # Application entity
│   │   │   │   ├── Tenant.java                # Tenant entity
│   │   │   │   ├── User.java                  # User entity
│   │   │   │   ├── OperationResult.java       # Result wrapper
│   │   │   │   └── OutputFormat.java          # Output format enum
│   │   │   │
│   │   │   ├── output/           # Output formatting
│   │   │   │   ├── OutputFormatter.java       # Main formatter
│   │   │   │   ├── JsonFormatter.java         # JSON output
│   │   │   │   └── TextFormatter.java         # Human-readable output
│   │   │   │
│   │   │   ├── service/          # Business logic and SDK integration
│   │   │   │   ├── DescopeService.java        # Base service
│   │   │   │   ├── ApplicationService.java    # Application operations
│   │   │   │   ├── TenantService.java         # Tenant operations
│   │   │   │   └── UserService.java           # User operations
│   │   │   │
│   │   │   └── Main.java         # Application entry point
│   │   │
│   │   └── resources/
│   │       └── application.properties         # Quarkus configuration
│   │
│   └── test/
│       ├── java/com/descope/utils/
│       │   ├── cli/              # CLI unit tests
│       │   ├── config/           # Configuration tests
│       │   ├── integration/      # Integration tests
│       │   ├── model/            # Model tests
│       │   ├── output/           # Formatter tests
│       │   └── service/          # Service tests
│       │
│       └── resources/
│           └── application-test.properties
│
├── build.gradle                   # Gradle build configuration
├── settings.gradle                # Gradle settings
├── README.md                      # User documentation
└── CONTRIBUTING.md                # This file
```

## Adding New Features

### Adding a New CLI Command

1. **Create Command Class** in `src/main/java/com/descope/utils/cli/`

```java
@Command(
    name = "command-name",
    description = "Description of what this command does",
    mixinStandardHelpOptions = true)
public class NewCommand implements Callable<Integer> {

  @Inject
  SomeService service;

  @Inject
  ConfigurationService configService;

  @Inject
  OutputFormatter outputFormatter;

  @Parameters(index = "0", description = "Parameter description")
  private String param;

  @Override
  public Integer call() {
    try {
      // 1. Load configuration
      DescopeConfig config = configService.loadConfig();

      // 2. Execute business logic
      OperationResult<Entity> result = service.doSomething(param);

      // 3. Format and display output
      outputFormatter.format(result);

      return result.isSuccess() ? 0 : 1;
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      return 1;
    }
  }
}
```

2. **Add to DescopeUtilsCommand** subcommands list

```java
@Command(
    name = "descope-utils",
    subcommands = {
        CreateAppCommand.class,
        CreateTenantCommand.class,
        CreateUserCommand.class,
        NewCommand.class  // Add here
    })
```

3. **Create Tests**

```java
@QuarkusTest
public class NewCommandTest {

  @InjectMock
  SomeService service;

  @Inject
  NewCommand command;

  @Test
  @DisplayName("doSomething - valid input - returns success")
  void doSomething_validInput_returnsSuccess() {
    // Test implementation
  }
}
```

4. **Update Documentation**
   - Add usage examples to README.md
   - Add JavaDoc to the command class

### Adding a New Service Method

1. **Add Method to Service Class**

```java
/**
 * Does something with the Descope API.
 *
 * @param param The parameter
 * @return OperationResult with the result
 */
public OperationResult<Entity> doSomething(String param) {
  logger.info("Doing something with: {}", param);

  try {
    DescopeClient client = descopeService.createClient();
    // SDK calls here

    return OperationResult.success(entity, "Success message");
  } catch (Exception e) {
    throw descopeService.wrapException("do something", e);
  }
}
```

2. **Add Unit Tests**

```java
@Test
@DisplayName("doSomething - valid param - returns success result")
void doSomething_validParam_returnsSuccess() {
  // Arrange
  when(mockClient.someMethod()).thenReturn(expectedValue);

  // Act
  OperationResult<Entity> result = service.doSomething("param");

  // Assert
  assertThat(result.isSuccess()).isTrue();
  assertThat(result.getData()).isEqualTo(expected);
}
```

3. **Add Integration Tests** (if needed)

## Common Issues and Solutions

### Build Issues

**Issue:** `Execution failed for task ':spotlessJavaApply'`

**Solution:** Run `./gradlew spotlessApply` to auto-format code before building.

---

**Issue:** `Test coverage is below 80%`

**Solution:** Add more tests to cover untested code paths. Use JaCoCo reports to identify gaps.

---

**Issue:** `Dependency resolution failed`

**Solution:** Run `./gradlew clean build --refresh-dependencies` to refresh dependencies.

### Test Issues

**Issue:** `Tests fail with NullPointerException in CDI injection`

**Solution:** Ensure you're using `@QuarkusTest` annotation and proper `@Inject` or `@InjectMock` annotations.

---

**Issue:** `Integration tests fail with authentication error`

**Solution:** Ensure valid Descope credentials are configured in test properties or environment variables.

## Getting Help

- **Questions**: Open a GitHub Discussion
- **Bugs**: Open a GitHub Issue with reproduction steps
- **Feature Requests**: Open a GitHub Issue with detailed description
- **Security Issues**: Email security@[project-domain] (do not open public issues)

## Recognition

Contributors will be recognized in:
- Git commit history
- Release notes
- Project documentation (if significant contribution)

Thank you for contributing to Descope Management CLI Utilities!
