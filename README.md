# Ensek API Test Automation Suite

A comprehensive test automation framework for the Ensek Energy Trading Platform API, built using Java, Maven, TestNG, and REST Assured.

## Overview

This project was developed as part of a take-home technical assessment for Ensek, focusing on API testing for their energy trading platform. The test suite validates various API endpoints including energy purchasing, order management, authentication, and data retrieval.

## Project Structure

```
ensek/
├── src/
│   ├── main/java/com/ensek/          # Main application code (if any)
│   └── test/java/com/ensek/Api/      # Test automation code
│       ├── BaseApiTest.java          # Base test class with common setup
│       ├── EnsekApiTests.java        # Original test implementation
│       ├── EnsekApiTestsRefactored.java  # Improved test implementation
│       └── utils/                    # Utility classes
│           ├── ConfigUtils.java      # Configuration management
│           ├── DataUtils.java        # Data manipulation utilities
│           ├── RestAssuredUtils.java # REST Assured configuration
│           ├── TestReportUtils.java  # Test reporting utilities
│           └── TestUtilities.java    # General test utilities
├── src/test/resources/
│   ├── config.properties             # Test configuration
│   └── logback-test.xml             # Logging configuration
├── target/                          # Maven build output
├── TestNG.xml                       # TestNG suite configuration
├── pom.xml                         # Maven project configuration
└── README.md                       # This file
```

## Features

### 🚀 **Enhanced Test Framework**
- **Base Test Class**: Common setup and teardown logic
- **Configuration Management**: Externalized configuration via properties file
- **Comprehensive Logging**: Structured logging with Logback
- **Utility Classes**: Reusable components for API testing
- **Data Providers**: Parameterized test data for comprehensive coverage

### 🧪 **Test Coverage**
- **Positive Tests**: Happy path scenarios
- **Negative Tests**: Error condition validation
- **Authentication Tests**: Security and authorization
- **Edge Cases**: Boundary conditions and corner cases
- **Data Validation**: Response structure and content verification

### 📊 **Reporting & Monitoring**
- **TestNG Reports**: Built-in HTML and XML reports
- **Custom Logging**: Detailed API request/response logging
- **Maven Surefire**: Integration with Maven for CI/CD
- **Performance Monitoring**: Response time validation

## API Endpoints Tested

| Endpoint | Method | Description | Test Coverage |
|----------|--------|-------------|---------------|
| `/ENSEK/reset` | POST | Reset test data | ✅ Positive |
| `/ENSEK/buy/{id}/{quantity}` | PUT | Purchase energy | ✅ Positive, ✅ Negative, ✅ Edge |
| `/ENSEK/orders` | GET | Retrieve all orders | ✅ Positive, ✅ Auth |
| `/ENSEK/orders/{orderId}` | GET | Retrieve specific order | ✅ Positive, ✅ Edge |
| `/ENSEK/energy` | GET | Get energy types | ✅ Positive |
| `/ENSEK/login` | POST | User authentication | ✅ Positive, ✅ Negative |

## Prerequisites

- **Java 11** or higher
- **Maven 3.6+**
- **Internet connection** (for downloading dependencies)

## Configuration

### Environment Setup

1. **Update Configuration**: Edit `src/test/resources/config.properties`:
   ```properties
   # Update the base URL to point to your test environment
   base.url=https://ensekautomationcandidatetest.azurewebsites.net
   
   # Update authentication credentials
   auth.token=YOUR_ACTUAL_ACCESS_TOKEN
   test.username=your_test_username
   test.password=your_test_password
   ```

2. **Test Data Configuration**: Modify fuel IDs and test data as needed:
   ```properties
   fuel.id.valid=1
   fuel.id.test=123
   order.id.valid=ACTUAL_ORDER_ID
   ```

### Running Tests

#### Run All Tests
```bash
mvn clean test
```

#### Run Specific Test Groups
```bash
# Run only positive tests
mvn test -Dgroups=Positive

# Run only negative tests
mvn test -Dgroups=Negative

# Run authentication tests
mvn test -Dgroups=Authentication

# Run edge case tests
mvn test -Dgroups=Edge
```

#### Run with Custom TestNG Suite
```bash
mvn test -DsuiteXmlFile=TestNG.xml
```

#### Generate Test Reports
```bash
mvn clean test site
```

## Test Groups

| Group | Description | Test Cases |
|-------|-------------|------------|
| **Positive** | Happy path scenarios | Basic functionality validation |
| **Negative** | Error conditions | Invalid inputs, unauthorized access |
| **Authentication** | Security tests | Login, token validation |
| **Edge** | Boundary conditions | Zero quantities, non-existent IDs |
| **Setup** | Environment preparation | Data reset, initial configuration |

## Continuous Integration

### Maven Commands for CI/CD

```bash
# Complete build with tests
mvn clean compile test

# Generate reports
mvn surefire-report:report

# Package with test execution
mvn clean package

# Skip tests (for build verification)
mvn clean package -DskipTests
```

### Jenkins/GitHub Actions Integration

The project is ready for CI/CD integration with:
- Maven Surefire plugin for test execution
- XML and HTML report generation
- Configurable test groups for different environments
- Detailed logging for debugging

## Logging

### Log Files Location
- **Main Log**: `target/logs/ensek-api-tests.log`
- **Rolling Log**: `target/logs/ensek-api-tests-rolling.log`
- **API Requests**: `target/logs/api-requests.log`

### Log Levels
- **INFO**: General test execution information
- **WARN**: Non-critical issues and skipped tests
- **ERROR**: Test failures and critical issues
- **DEBUG**: Detailed execution traces (enable in logback-test.xml)

## Best Practices Implemented

### ✅ **Code Quality**
- Proper exception handling and logging
- Separation of concerns with utility classes
- Configurable timeouts and retry mechanisms
- Comprehensive error message extraction

### ✅ **Test Design**
- Data-driven testing with TestNG DataProviders
- Soft assertions for multiple validations
- Response time validation
- Content type verification

### ✅ **Maintainability**
- Externalized configuration
- Reusable utility methods
- Clear test documentation
- Consistent naming conventions

## Troubleshooting

### Common Issues

1. **Connection Timeout**
   ```
   Solution: Update timeout values in config.properties
   timeout.default=60
   ```

2. **Authentication Failures**
   ```
   Solution: Verify auth.token in config.properties
   Check token expiration and permissions
   ```

3. **Test Data Issues**
   ```
   Solution: Run testResetTestData first
   Verify fuel.id.* values in configuration
   ```

4. **Build Failures**
   ```bash
   # Clean and rebuild
   mvn clean compile
   
   # Update dependencies
   mvn dependency:resolve
   ```

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| TestNG | 7.11.0 | Test framework |
| REST Assured | 5.5.6 | API testing library |
| Jackson | 2.18.2 | JSON processing |
| Logback | 1.5.16 | Logging framework |
| SLF4J | 2.0.16 | Logging API |

## Future Enhancements

### 🔄 **Planned Improvements**
- [ ] JSON Schema validation
- [ ] Performance testing integration
- [ ] Database validation hooks
- [ ] Custom HTML report generation
- [ ] Allure reporting integration
- [ ] Docker containerization
- [ ] API contract testing
- [ ] Mock server integration for local testing

### 🧪 **Additional Test Scenarios**
- [ ] Concurrency testing
- [ ] Rate limiting validation
- [ ] Data integrity checks
- [ ] Cross-browser API testing
- [ ] Load testing integration

## Contributing

When extending this test suite:

1. Follow the existing code structure
2. Add appropriate logging and error handling
3. Update configuration properties as needed
4. Add comprehensive test documentation
5. Include both positive and negative test cases
6. Update this README with any new features

## License

This project was created for educational and assessment purposes.

---

**Note**: This test suite was developed as part of a technical assessment for Ensek. The actual API endpoints and authentication mechanisms should be updated based on the current Ensek test environment configuration.