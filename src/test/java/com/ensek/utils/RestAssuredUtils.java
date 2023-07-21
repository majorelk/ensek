package com.ensek.utils;

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
}
