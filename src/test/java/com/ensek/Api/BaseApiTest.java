package com.ensek.Api;

import com.ensek.Api.utils.ConfigUtils;
import com.ensek.Api.utils.TestReportUtils;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Base test class for all Ensek API tests
 * Provides common setup, configuration, and utility methods
 */
public class BaseApiTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);
    protected RequestSpecification requestSpec;
    protected RequestSpecification invalidAuthRequestSpec;
    
    @BeforeClass
    public void setupClass() {
        logger.info("Setting up API test configuration");
        
        // Configure RestAssured base settings
        RestAssured.baseURI = ConfigUtils.getBaseUrl();
        RestAssured.basePath = ConfigUtils.getBasePath();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Build request specification with valid authentication
        requestSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + ConfigUtils.getAuthToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();\n        \n        // Build request specification with invalid authentication for negative tests\n        invalidAuthRequestSpec = new RequestSpecBuilder()\n                .addHeader("Authorization", "Bearer INVALID_TOKEN")\n                .addHeader("Content-Type", "application/json")\n                .addHeader("Accept", "application/json")\n                .build();\n        \n        logger.info("API test configuration completed. Base URL: {}", ConfigUtils.getBaseUrl());\n    }\n    \n    @BeforeMethod\n    public void setupMethod(Method method) {\n        logger.info("Starting test: {}", method.getName());\n    }\n    \n    @AfterMethod\n    public void teardownMethod(ITestResult result) {\n        TestReportUtils.logTestStatus(result);\n        \n        if (result.getStatus() == ITestResult.FAILURE) {\n            logger.error("Test failed: {} - {}", result.getMethod().getMethodName(), result.getThrowable().getMessage());\n        } else if (result.getStatus() == ITestResult.SUCCESS) {\n            logger.info("Test passed: {}", result.getMethod().getMethodName());\n        } else if (result.getStatus() == ITestResult.SKIP) {\n            logger.warn("Test skipped: {}", result.getMethod().getMethodName());\n        }\n    }\n    \n    /**\n     * Helper method to get request specification with custom auth token\n     */\n    protected RequestSpecification getRequestSpecWithCustomAuth(String token) {\n        return new RequestSpecBuilder()\n                .addHeader("Authorization", "Bearer " + token)\n                .addHeader("Content-Type", "application/json")\n                .addHeader("Accept", "application/json")\n                .build();\n    }\n    \n    /**\n     * Helper method to log API call details\n     */\n    protected void logApiCall(String method, String endpoint) {\n        logger.info("Making {} request to: {}", method, endpoint);\n    }\n    \n    /**\n     * Helper method to log API response details\n     */\n    protected void logApiResponse(int statusCode, String endpoint) {\n        logger.info("Response from {}: Status Code = {}", endpoint, statusCode);\n    }\n}