package com.ensek.Api.utils;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RestAssuredUtils {

    private static RequestSpecification requestSpec;

    public static void setupRequestSpecification(String baseURI, int port, String basePath, String authToken) {
        RestAssured.baseURI = baseURI;
        RestAssured.port = port;
        RestAssured.basePath = basePath;

        requestSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
    }

    public static RequestSpecification getRequestSpecification() {
        return requestSpec;
    }

    private static final String INVALID_TOKEN = "INVALID_ACCESS_TOKEN";

    // Method to get a RequestSpecification object with a valid access token
    public static RequestSpecification getRequestSpecificationWithValidToken() {
        String validAccessToken = "YOUR_VALID_ACCESS_TOKEN"; // Replace with your actual valid access token
        return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + validAccessToken)
                .build();
    }

    public static RequestSpecification getRequestSpecificationWithInvalidToken(RequestSpecification requestSpec, String invalidToken) {
        return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + INVALID_TOKEN)
                .build();
    }
}
