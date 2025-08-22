#!/bin/bash

# Ensek API Test Suite - Setup Validation Script
# This script validates that all prerequisites are installed and configured

echo "============================================"
echo "Ensek API Test Suite - Setup Validation"
echo "============================================"
echo

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print success message
print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

# Function to print error message
print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Function to print warning message
print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Check Java
echo "1. Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d. -f1)
    
    if [ "$JAVA_MAJOR_VERSION" -ge 11 ]; then
        print_success "Java $JAVA_VERSION is installed and compatible"
    else
        print_error "Java $JAVA_VERSION is installed but version 11+ is required"
        echo "  Install Java 11 or higher to continue"
    fi
else
    print_error "Java is not installed"
    echo "  Install Java 11+ with: sudo apt install openjdk-11-jdk (Ubuntu/Debian)"
    echo "  Or download from: https://adoptium.net/"
fi
echo

# Check Maven
echo "2. Checking Maven installation..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version 2>/dev/null | grep "Apache Maven" | cut -d' ' -f3)
    print_success "Maven $MVN_VERSION is installed"
else
    print_error "Maven is not installed"
    echo "  Install Maven with: sudo apt install maven (Ubuntu/Debian)"
    echo "  Or download from: https://maven.apache.org/download.cgi"
fi
echo

# Check Git
echo "3. Checking Git installation..."
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | cut -d' ' -f3)
    print_success "Git $GIT_VERSION is installed"
else
    print_warning "Git is not installed (optional for running tests)"
    echo "  Install Git with: sudo apt install git"
fi
echo

# Check project structure
echo "4. Checking project structure..."
if [ -f "pom.xml" ]; then
    print_success "pom.xml found"
else
    print_error "pom.xml not found - ensure you're in the project root directory"
fi

if [ -f "TestNG.xml" ]; then
    print_success "TestNG.xml found"
else
    print_error "TestNG.xml not found"
fi

if [ -d "src/test/java/com/ensek/Api" ]; then
    print_success "Test source directory found"
else
    print_error "Test source directory not found"
fi

if [ -f "src/test/resources/config.properties" ]; then
    print_success "Configuration file found"
else
    print_error "Configuration file not found"
fi
echo

# Check configuration
echo "5. Checking configuration..."
if [ -f "src/test/resources/config.properties" ]; then
    # Check if default values are still present
    if grep -q "YOUR_ACCESS_TOKEN" src/test/resources/config.properties; then
        print_warning "Default auth token found in config.properties"
        echo "  Update auth.token with a valid token"
    else
        print_success "Auth token appears to be configured"
    fi
    
    if grep -q "ORDER_ID_TO_TEST" src/test/resources/config.properties; then
        print_warning "Default order ID found in config.properties"
        echo "  Update order.id.valid with a real order ID"
    fi
    
    BASE_URL=$(grep "^base.url=" src/test/resources/config.properties | cut -d'=' -f2)
    if [ -n "$BASE_URL" ]; then
        print_success "Base URL configured: $BASE_URL"
    else
        print_error "Base URL not configured"
    fi
fi
echo

# Test connectivity
echo "6. Testing network connectivity..."
if [ -f "src/test/resources/config.properties" ]; then
    BASE_URL=$(grep "^base.url=" src/test/resources/config.properties | cut -d'=' -f2)
    if command -v curl &> /dev/null && [ -n "$BASE_URL" ]; then
        if curl -Is --connect-timeout 10 "$BASE_URL" | head -1 | grep -q "200\|301\|302"; then
            print_success "Successfully connected to $BASE_URL"
        else
            print_warning "Unable to connect to $BASE_URL"
            echo "  Check your internet connection and URL configuration"
        fi
    else
        print_warning "Curl not available or base URL not configured - skipping connectivity test"
    fi
else
    print_warning "Configuration file not found - skipping connectivity test"
fi
echo

# Summary
echo "============================================"
echo "Setup Validation Summary"
echo "============================================"

# Count issues
JAVA_OK=$(command -v java &> /dev/null && echo "1" || echo "0")
MVN_OK=$(command -v mvn &> /dev/null && echo "1" || echo "0")
POM_OK=$([ -f "pom.xml" ] && echo "1" || echo "0")
TESTNG_OK=$([ -f "TestNG.xml" ] && echo "1" || echo "0")
CONFIG_OK=$([ -f "src/test/resources/config.properties" ] && echo "1" || echo "0")

TOTAL_CHECKS=5
PASSED_CHECKS=$((JAVA_OK + MVN_OK + POM_OK + TESTNG_OK + CONFIG_OK))

echo "Passed: $PASSED_CHECKS/$TOTAL_CHECKS checks"

if [ "$PASSED_CHECKS" -eq "$TOTAL_CHECKS" ]; then
    print_success "All checks passed! You're ready to run the tests."
    echo
    echo "Next steps:"
    echo "1. Update configuration: vim src/test/resources/config.properties"
    echo "2. Run tests: mvn clean test"
    echo "3. View reports: open target/surefire-reports/index.html"
elif [ "$PASSED_CHECKS" -ge 3 ]; then
    print_warning "Most checks passed, but some issues need attention."
    echo "Review the errors above and install missing components."
else
    print_error "Several critical issues found."
    echo "Install Java and Maven before proceeding."
fi

echo
echo "For detailed setup instructions, see: SETUP_GUIDE.md"
echo "For project documentation, see: README.md"