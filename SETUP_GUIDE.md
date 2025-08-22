# Quick Setup Guide

This guide will help you get the Ensek API Test Suite up and running quickly.

## Prerequisites Check

Verify you have the required tools installed:

```bash
# Check Java version (should be 11+)
java -version

# Check Maven version (should be 3.6+)
mvn -version

# Check Git (for version control)
git --version
```

## Quick Start (5 Minutes)

### 1. Clone and Navigate
```bash
git clone <repository-url>
cd ensek
```

### 2. Update Configuration
Edit `src/test/resources/config.properties`:
```properties
# Update these values for your environment
base.url=https://ensekautomationcandidatetest.azurewebsites.net
auth.token=YOUR_ACTUAL_TOKEN_HERE
test.username=your_username
test.password=your_password
```

### 3. Run Tests
```bash
# Clean build and run all tests
mvn clean test

# Or run specific test groups
mvn test -Dgroups=Positive
```

### 4. View Results
- **Console Output**: Real-time test results
- **HTML Report**: `target/surefire-reports/index.html`
- **XML Report**: `target/surefire-reports/TEST-*.xml`
- **Logs**: `target/logs/ensek-api-tests.log`

## Common Setup Issues

### Issue: Tests Fail with 401 Unauthorized
```bash
# Solution: Update your auth token
vim src/test/resources/config.properties
# Update auth.token=YOUR_VALID_TOKEN
```

### Issue: Connection Timeouts
```bash
# Solution: Increase timeout values
vim src/test/resources/config.properties
# Update timeout.default=60
```

### Issue: Maven Dependency Problems
```bash
# Solution: Clean and reload dependencies
mvn clean dependency:resolve
mvn compile
```

## Development Setup

### IDE Configuration

#### IntelliJ IDEA
1. Open project as Maven project
2. Set Project SDK to Java 11+
3. Enable annotation processing
4. Install TestNG plugin

#### Eclipse
1. Import as Maven project
2. Set Java Build Path to JDK 11+
3. Install TestNG plugin from marketplace

### Running Tests in IDE
1. Right-click on `TestNG.xml`
2. Select "Run As" → "TestNG Suite"
3. Or run individual test classes/methods

## Environment-Specific Configuration

### Local Development
```properties
base.url=http://localhost:8080
auth.token=DEV_TOKEN
timeout.default=10
```

### CI/CD Environment
```properties
base.url=${API_BASE_URL}
auth.token=${API_AUTH_TOKEN}
timeout.default=30
```

Use environment variables in CI/CD:
```bash
export API_BASE_URL=https://test.ensek.com
export API_AUTH_TOKEN=ci_token_here
mvn test
```

## Test Execution Options

### Run All Tests
```bash
mvn clean test
```

### Run by Group
```bash
# Positive tests only
mvn test -Dgroups=Positive

# Negative tests only
mvn test -Dgroups=Negative

# Authentication tests
mvn test -Dgroups=Authentication
```

### Run Specific Test Class
```bash
mvn test -Dtest=EnsekApiTestsRefactored
```

### Run with Custom Properties
```bash
mvn test -Dconfig.file=src/test/resources/prod-config.properties
```

### Generate Reports
```bash
mvn clean test site
open target/site/surefire-report.html
```

## Debugging

### Enable Debug Logging
Edit `src/test/resources/logback-test.xml`:
```xml
<root level="DEBUG">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</root>
```

### Run Single Test with Debug
```bash
mvn test -Dtest=EnsekApiTestsRefactored#testLogin -X
```

### View REST Assured Logs
Add to test code temporarily:
```java
given()
    .log().all()  // Log request
    .spec(requestSpec)
    .when()
    .get("/ENSEK/energy")
    .then()
    .log().all(); // Log response
```

## Directory Structure Overview

```
ensek/
├── src/test/java/com/ensek/Api/     # Test code
├── src/test/resources/              # Config files
├── target/                          # Build output
│   ├── logs/                       # Log files
│   └── surefire-reports/           # Test reports
├── TestNG.xml                      # Test suite config
└── pom.xml                         # Maven config
```

## Next Steps

1. **Customize Configuration**: Update config.properties for your environment
2. **Add New Tests**: Extend existing test classes or create new ones
3. **CI/CD Integration**: Set up Jenkins/GitHub Actions pipeline
4. **Reporting**: Integrate with Allure or other reporting tools
5. **Monitoring**: Set up alerts for test failures

## Need Help?

- Check the logs in `target/logs/`
- Review API responses in debug mode
- Verify network connectivity to API endpoints
- Confirm authentication tokens are valid
- Check Maven dependencies are resolved

## Useful Commands Reference

```bash
# Clean everything and start fresh
mvn clean

# Compile without running tests
mvn compile

# Run tests with verbose output
mvn test -X

# Skip tests during build
mvn package -DskipTests

# Force dependency update
mvn dependency:resolve -U

# Generate test report site
mvn site

# Run tests in parallel
mvn test -DforkCount=4 -DreuseForks=true
```

---

You're now ready to run the Ensek API Test Suite! 🚀