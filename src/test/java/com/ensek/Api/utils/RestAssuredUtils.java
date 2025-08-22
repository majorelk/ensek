package com.ensek.Api.utils;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for RestAssured configuration and common request specifications
 */
public class RestAssuredUtils {

    private static final Logger logger = LoggerFactory.getLogger(RestAssuredUtils.class);
    private static final String INVALID_TOKEN = "INVALID_ACCESS_TOKEN";
    private static RequestSpecification baseRequestSpec;

    /**
     * Setup RestAssured with base configuration
     */
    public static void setupRestAssuredConfig() {
        // Configure timeouts and other settings
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ConfigUtils.getDefaultTimeout() * 1000)
                        .setParam("http.socket.timeout", ConfigUtils.getDefaultTimeout() * 1000));
        
        logger.info("RestAssured configuration setup completed");
    }

    /**
     * Setup base request specification with provided parameters
     */
    public static void setupRequestSpecification(String baseURI, String basePath, String authToken) {
        RestAssured.baseURI = baseURI;
        RestAssured.basePath = basePath;

        baseRequestSpec = createRequestSpecification(authToken);
        logger.info("Request specification setup completed for base URI: {}", baseURI);
    }

    /**
     * Get the base request specification
     */
    public static RequestSpecification getRequestSpecification() {
        if (baseRequestSpec == null) {
            throw new IllegalStateException("Request specification not initialized. Call setupRequestSpecification first.");
        }
        return baseRequestSpec;
    }

    /**
     * Create a request specification with the given auth token
     */
    public static RequestSpecification createRequestSpecification(String authToken) {
        return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + authToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "Ensek-API-Test-Suite/1.0")
                .build();
    }

    /**
     * Get a RequestSpecification object with a valid access token from config
     */
    public static RequestSpecification getRequestSpecificationWithValidToken() {
        return createRequestSpecification(ConfigUtils.getAuthToken());
    }

    /**
     * Get a RequestSpecification object with an invalid token for negative testing
     */
    public static RequestSpecification getRequestSpecificationWithInvalidToken() {
        return createRequestSpecification(INVALID_TOKEN);
    }

    /**
     * Get a RequestSpecification object with a custom invalid token
     */
    public static RequestSpecification getRequestSpecificationWithInvalidToken(RequestSpecification requestSpec, String invalidToken) {
        return createRequestSpecification(invalidToken);
    }

    /**
     * Get a RequestSpecification object without any authorization header
     */
    public static RequestSpecification getRequestSpecificationWithoutAuth() {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "Ensek-API-Test-Suite/1.0")
                .build();
    }

    /**
     * Get a RequestSpecification object with custom headers
     */
    public static RequestSpecification getRequestSpecificationWithCustomHeaders(String authToken, 
                                                                               String contentType, 
                                                                               String accept) {
        return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + authToken)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", accept)
                .addHeader("User-Agent", "Ensek-API-Test-Suite/1.0")
                .build();
    }

    /**
     * Reset RestAssured to default configuration
     */
    public static void resetRestAssured() {
        RestAssured.reset();
        logger.info("RestAssured configuration reset to defaults");
    }
}
