package com.ensek.Api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.ensek.utils.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Map;

public class EnsekApiTests {

    private static final int YOUR_FUEL_ID = 123;
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
        // Get the list of energy types
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/ENSEK/energy");

        // Extract the energy types JSON response as a String
        String responseBody = response.getBody().asString();

        // Parse the JSON response using JsonPath
        JsonPath jsonPath = new JsonPath(responseBody);

        // Choose a fuel ID (replace 'YOUR_FUEL_ID' with the desired fuel ID to test)
        int fuelId = YOUR_FUEL_ID;

        // Fetch the available quantity for the selected fuel ID
        int availableQuantity = jsonPath.getInt("findAll { it.id == " + fuelId + " }.quantity_available");

        // Test buying more than the available quantity
        given()
                .spec(requestSpec)
                .pathParam("id", fuelId)
                .pathParam("quantity", availableQuantity + 1)
                .when()
                .put("/ENSEK/buy/{id}/{quantity}")
                .then()
                .statusCode(400);
    }

    @Test(groups = "Negative")
    public void testBuyWhenOutOfStock() {
        // Get the list of energy types
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/ENSEK/energy");

        // Parse the JSON response using JsonPath
        JsonPath jsonPath = response.jsonPath();

        // Choose a fuel ID (replace 'YOUR_FUEL_ID' with the desired fuel ID to test)
        int fuelId = 10112;

        // Set the available quantity for the selected fuel ID to 0
        List<Map<String, Object>> energyTypes = jsonPath.getList("energyTypes");
        for (Map<String, Object> energyType : energyTypes) {
            if ((int) energyType.get("id") == fuelId) {
                energyType.put("quantity_available", 0);
                break;
            }
        }

        // Convert the updated JSON response back to a string
        String updatedResponseBody = jsonPath.prettify();

        // Test buying when the fuel is out of stock (quantity = 0)
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(updatedResponseBody)
                .when()
                .put("/ENSEK/energy")
                .then()
                .statusCode(400);
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
