package com.ensek.Api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class EnsekApiTests {

    private RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost"; // Replace with the base URL of your Ensek API
        RestAssured.port = 8080; // Replace with the port of your Ensek API
        RestAssured.basePath = "/"; // Replace with the base path of your Ensek API

        // Build request specification to include required headers, authentication, etc.
        requestSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer YOUR_ACCESS_TOKEN") // Replace with the actual access token
                .build();
    }

    @Test(groups = "Positive")
    public void testResetTestData() {
        given()
                .spec(requestSpec)
                .when()
                .post("/ENSEK/reset")
                .then()
                .statusCode(200);
    }

    @Test(groups = "Positive", dataProvider = "buyFuelData")
    public void testBuyFuel(int id, int quantity) {
        given()
                .spec(requestSpec)
                .pathParam("id", id)
                .pathParam("quantity", quantity)
                .when()
                .put("/ENSEK/buy/{id}/{quantity}")
                .then()
                .statusCode(200);
    }

    @Test(groups = "Positive")
    public void testGetOrders() {
        given()
                .spec(requestSpec)
                .when()
                .get("/ENSEK/orders")
                .then()
                .statusCode(200);
        // Add assertions to verify the response body if required
    }

    @Test(groups = "Positive")
    public void testGetSingleOrder() {
        String orderId = "ORDER_ID_TO_TEST"; // Replace with the order ID to test
        given()
                .spec(requestSpec)
                .pathParam("orderId", orderId)
                .when()
                .get("/ENSEK/orders/{orderId}")
                .then()
                .statusCode(200)
                .body("orderId", equalTo(orderId)); // Assertion using equalTo
    }

    @Test(groups = "Positive")
    public void testEnergyTypes() {
        given()
                .spec(requestSpec)
                .when()
                .get("/ENSEK/energy")
                .then()
                .statusCode(200);
        // Add assertions to verify the response body if required
    }

    @Test(groups = "Positive")
    public void testLogin() {
        given()
                .spec(requestSpec)
                .body("{\"username\": \"test\", \"password\": \"testing\"}") // Replace with actual username and password
                .when()
                .post("/ENSEK/login")
                .then()
                .statusCode(200);
    }

    @Test(groups = "Negative")
    public void testInvalidBuyFuel() {
        // Test implementation for negative buy fuel scenario
    }

    @Test(groups = "Negative")
    public void testUnauthorizedAccess() {
        // Test implementation for negative unauthorized access scenario
    }

    @Test(groups = "Negative")
    public void testBuyMoreThanAvailable() {
        // Test implementation for negative buy more than available scenario
    }

    @Test(groups = "Negative")
    public void testBuyWhenOutOfStock() {
        // Test implementation for negative buy when out of stock scenario
    }

    @Test(groups = "Auth")
    public void testUnauthorizedLogin() {
        // Test implementation for unauthorized login scenario
    }

    @Test(groups = "Auth")
    public void testInvalidTokenAccess() {
        // Test implementation for invalid token access scenario
    }

    @Test(groups = "Edge")
    public void testBuyZeroFuel() {
        // Test implementation for edge case of buying zero fuel
    }

    @Test(groups = "Edge")
    public void testGetOrderByIdNotFound() {
        // Test implementation for edge case of getting order by non-existent ID
    }

    // DataProvider method for "testBuyFuel" test
    @DataProvider(name = "buyFuelData")
    public Object[][] buyFuelData() {
        return new Object[][]{
                {1, 10},
                {2, 5},
                {3, 8}
        };
    }
}
