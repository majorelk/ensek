package com.ensek.Api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.ensek.utils.*;

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
        int invalidId = -1;
        int invalidQuantity = -10;

        given()
                .spec(requestSpec)
                .pathParam("id", invalidId)
                .pathParam("quantity", invalidQuantity)
                .when()
                .put("/ENSEK/buy/{id}/{quantity}")
                .then()
                .statusCode(400);
    }

    @Test(groups = "Negative")
    public void testUnauthorizedAccess() {
        String invalidToken = "INVALID_TOKEN";
        RequestSpecification requestSpecWithInvalidToken = RestAssuredUtils.getRequestSpecificationWithInvalidToken(requestSpec, invalidToken);

        given()
                .spec(requestSpecWithInvalidToken)
                .when()
                .get("/ENSEK/orders")
                .then()
                .statusCode(401);
    }

    @Test(groups = "Negative")
    public void testBuyMoreThanAvailable() {
        // You need to get the available quantity for the given fuel ID and test buying more than that.
        // Use the DataUtils class to fetch the available quantity based on the fuel ID from the Swagger model.
        // Then use RestAssured to test the buy fuel API with a quantity greater than the available quantity.
    }

    @Test(groups = "Negative")
    public void testBuyWhenOutOfStock() {
        // Similar to the previous test, but this time set the available quantity to 0 and test buying the fuel.
    }

    @Test(groups = "Auth")
    public void testUnauthorizedLogin() {
        String invalidUsername = "invaliduser";
        String invalidPassword = "invalidpassword";

        given()
                .spec(requestSpec)
                .body("{\"username\": \"" + invalidUsername + "\", \"password\": \"" + invalidPassword + "\"}")
                .when()
                .post("/ENSEK/login")
                .then()
                .statusCode(401);
    }

    @Test(groups = "Auth")
    public void testInvalidTokenAccess() {
        String invalidToken = "INVALID_TOKEN";
        RequestSpecification requestSpecWithInvalidToken = RestAssuredUtils.getRequestSpecificationWithInvalidToken(requestSpec, invalidToken);

        given()
                .spec(requestSpecWithInvalidToken)
                .when()
                .get("/ENSEK/orders")
                .then()
                .statusCode(401);
    }

    @Test(groups = "Edge")
    public void testBuyZeroFuel() {
        int fuelId = 1;
        int zeroQuantity = 0;

        given()
                .spec(requestSpec)
                .pathParam("id", fuelId)
                .pathParam("quantity", zeroQuantity)
                .when()
                .put("/ENSEK/buy/{id}/{quantity}")
                .then()
                .statusCode(200);
        // Add assertions to verify the response body if required
    }

    @Test(groups = "Edge")
    public void testGetOrderByIdNotFound() {
        String nonExistentOrderId = "NON_EXISTENT_ORDER_ID";

        given()
                .spec(requestSpec)
                .pathParam("orderId", nonExistentOrderId)
                .when()
                .get("/ENSEK/orders/{orderId}")
                .then()
                .statusCode(404);
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
