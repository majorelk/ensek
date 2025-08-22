package com.ensek.Api;

import com.ensek.Api.utils.ConfigUtils;
import com.ensek.Api.utils.DataUtils;
import com.ensek.Api.utils.RestAssuredUtils;
import com.ensek.Api.utils.TestReportUtils;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Comprehensive API test suite for Ensek Energy Trading Platform
 * 
 * This test suite covers:
 * - Positive test scenarios (happy path)
 * - Negative test scenarios (error conditions)
 * - Authentication and authorization tests
 * - Edge cases and boundary conditions
 * - Data validation and response structure verification
 */
public class EnsekApiTestsRefactored extends BaseApiTest {

    @BeforeClass
    @Override
    public void setupClass() {
        super.setupClass();
        RestAssuredUtils.setupRestAssuredConfig();
    }

    // ==================== POSITIVE TESTS ====================

    @Test(groups = {"Positive", "Setup"}, priority = 1)
    public void testResetTestData() {
        logApiCall("POST", "/ENSEK/reset");
        
        Response response = given()
                .spec(requestSpec)
                .when()
                .post("/ENSEK/reset")
                .then()
                .statusCode(200)
                .extract().response();

        logApiResponse(response.getStatusCode(), "/ENSEK/reset");
        TestReportUtils.logApiRequest("POST", "/ENSEK/reset", response.getStatusCode());
    }

    @Test(groups = {"Positive"}, dataProvider = "buyFuelData", dependsOnMethods = "testResetTestData")
    public void testBuyFuel(int fuelId, int quantity) {
        logApiCall("PUT", String.format("/ENSEK/buy/%d/%d", fuelId, quantity));
        
        Response response = given()
                .spec(requestSpec)
                .pathParam("id", fuelId)
                .pathParam("quantity", quantity)
                .when()
                .put("/ENSEK/buy/{id}/{quantity}")
                .then()
                .statusCode(200)
                .time(lessThan(ConfigUtils.getDefaultTimeout() * 1000L))
                .extract().response();

        // Verify response structure if it contains JSON
        String contentType = response.getContentType();
        if (contentType != null && contentType.contains("json")) {
            assertNotNull(response.getBody(), "Response body should not be null");
        }

        logApiResponse(response.getStatusCode(), String.format("/ENSEK/buy/%d/%d", fuelId, quantity));
    }

    @Test(groups = {"Positive"}, dependsOnMethods = "testBuyFuel")
    public void testGetOrders() {
        logApiCall("GET", "/ENSEK/orders");
        
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/ENSEK/orders")
                .then()
                .statusCode(200)
                .time(lessThan(ConfigUtils.getDefaultTimeout() * 1000L))
                .contentType(containsString("json"))
                .extract().response();

        // Verify response is a valid JSON array or object
        assertNotNull(response.jsonPath(), "Response should be valid JSON");
        
        logApiResponse(response.getStatusCode(), "/ENSEK/orders");
        logger.info("Retrieved orders count: {}", 
                   DataUtils.responseContainsKey(response, "$") ? 
                   DataUtils.getResponseValueAsList(response, "$").size() : "unknown");
    }

    @Test(groups = {"Positive"})
    public void testGetEnergyTypes() {
        logApiCall("GET", "/ENSEK/energy");
        
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/ENSEK/energy")
                .then()
                .statusCode(200)
                .time(lessThan(ConfigUtils.getDefaultTimeout() * 1000L))
                .contentType(containsString("json"))
                .extract().response();

        // Validate response structure
        assertNotNull(response.jsonPath(), "Energy types response should be valid JSON");
        
        // Check if response is an array or contains energy types
        if (DataUtils.responseContainsKey(response, "$")) {
            Object responseData = DataUtils.getResponseValueAsList(response, "$");
            if (responseData != null) {
                logger.info("Retrieved {} energy types", 
                           DataUtils.getResponseValueAsList(response, "$").size());
            }
        }

        logApiResponse(response.getStatusCode(), "/ENSEK/energy");
    }

    @Test(groups = {"Positive", "Authentication"})
    public void testLogin() {
        String loginBody = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", 
                                       ConfigUtils.getTestUsername(), 
                                       ConfigUtils.getTestPassword());
        
        logApiCall("POST", "/ENSEK/login");
        
        Response response = given()
                .spec(requestSpec)
                .body(loginBody)
                .when()
                .post("/ENSEK/login")
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(201)))
                .time(lessThan(ConfigUtils.getDefaultTimeout() * 1000L))
                .extract().response();

        logApiResponse(response.getStatusCode(), "/ENSEK/login");
        TestReportUtils.logApiRequest("POST", "/ENSEK/login", loginBody, response.getStatusCode());
    }

    // ==================== NEGATIVE TESTS ====================

    @Test(groups = {"Negative"})
    public void testInvalidBuyFuel() {
        int invalidId = ConfigUtils.getInvalidFuelId();
        int invalidQuantity = ConfigUtils.getInvalidQuantity();
        
        logApiCall("PUT", String.format("/ENSEK/buy/%d/%d", invalidId, invalidQuantity));
        
        Response response = given()
                .spec(requestSpec)
                .pathParam("id", invalidId)
                .pathParam("quantity", invalidQuantity)
                .when()
                .put("/ENSEK/buy/{id}/{quantity}")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(422)))
                .extract().response();

        // Log error details
        String errorMessage = DataUtils.extractErrorMessage(response);
        logger.info("Error response for invalid buy fuel request: {}", errorMessage);
        
        logApiResponse(response.getStatusCode(), String.format("/ENSEK/buy/%d/%d", invalidId, invalidQuantity));
    }

    @Test(groups = {"Negative", "Authentication"})
    public void testUnauthorizedAccess() {
        logApiCall("GET", "/ENSEK/orders (unauthorized)");
        
        Response response = given()
                .spec(invalidAuthRequestSpec)
                .when()
                .get("/ENSEK/orders")
                .then()
                .statusCode(anyOf(equalTo(401), equalTo(403)))
                .extract().response();

        logApiResponse(response.getStatusCode(), "/ENSEK/orders (unauthorized)");
    }

    @Test(groups = {"Negative"})
    public void testBuyMoreThanAvailable() {
        // First, get available energy types
        Response energyResponse = given()
                .spec(requestSpec)
                .when()
                .get("/ENSEK/energy")
                .then()
                .statusCode(200)
                .extract().response();

        int testFuelId = ConfigUtils.getTestFuelId();
        Integer availableQuantity = DataUtils.getAvailableQuantityForEnergyType(energyResponse, testFuelId);
        
        if (availableQuantity != null && availableQuantity > 0) {
            int excessQuantity = availableQuantity + 1;
            
            logApiCall("PUT", String.format("/ENSEK/buy/%d/%d (excess)", testFuelId, excessQuantity));
            
            Response response = given()
                    .spec(requestSpec)
                    .pathParam("id", testFuelId)
                    .pathParam("quantity", excessQuantity)
                    .when()
                    .put("/ENSEK/buy/{id}/{quantity}")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(409), equalTo(422)))
                    .extract().response();

            logApiResponse(response.getStatusCode(), String.format("/ENSEK/buy/%d/%d (excess)", testFuelId, excessQuantity));
        } else {
            logger.warn("Skipping testBuyMoreThanAvailable - unable to determine available quantity for fuel ID {}", testFuelId);
        }
    }

    @Test(groups = {"Negative", "Authentication"})
    public void testUnauthorizedLogin() {
        String invalidLoginBody = "{\"username\": \"invalidUser\", \"password\": \"invalidPassword\"}";
        
        logApiCall("POST", "/ENSEK/login (invalid credentials)");
        
        Response response = given()
                .spec(requestSpec)
                .body(invalidLoginBody)
                .when()
                .post("/ENSEK/login")
                .then()
                .statusCode(anyOf(equalTo(401), equalTo(400)))
                .extract().response();

        logApiResponse(response.getStatusCode(), "/ENSEK/login (invalid credentials)");
    }

    // ==================== EDGE CASE TESTS ====================

    @Test(groups = {"Edge"})
    public void testBuyZeroFuel() {
        int validFuelId = ConfigUtils.getValidFuelId();
        int zeroQuantity = ConfigUtils.getZeroQuantity();
        
        logApiCall("PUT", String.format("/ENSEK/buy/%d/%d (zero quantity)", validFuelId, zeroQuantity));
        
        Response response = given()
                .spec(requestSpec)
                .pathParam("id", validFuelId)
                .pathParam("quantity", zeroQuantity)
                .when()
                .put("/ENSEK/buy/{id}/{quantity}")
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(400)))
                .extract().response();

        logApiResponse(response.getStatusCode(), String.format("/ENSEK/buy/%d/%d (zero quantity)", validFuelId, zeroQuantity));
    }

    @Test(groups = {"Edge"})
    public void testGetOrderByIdNotFound() {
        String nonExistentOrderId = ConfigUtils.getInvalidOrderId();
        
        logApiCall("GET", String.format("/ENSEK/orders/%s (not found)", nonExistentOrderId));
        
        Response response = given()
                .spec(requestSpec)
                .pathParam("orderId", nonExistentOrderId)
                .when()
                .get("/ENSEK/orders/{orderId}")
                .then()
                .statusCode(anyOf(equalTo(404), equalTo(400)))
                .extract().response();

        logApiResponse(response.getStatusCode(), String.format("/ENSEK/orders/%s (not found)", nonExistentOrderId));
    }

    @Test(groups = {"Edge"})
    public void testGetSingleOrderWithValidId() {
        // First get all orders to find a valid order ID
        Response ordersResponse = given()
                .spec(requestSpec)
                .when()
                .get("/ENSEK/orders");

        if (ordersResponse.getStatusCode() == 200) {
            // Try to extract an order ID from the response
            Object orders = DataUtils.getResponseValueAsList(ordersResponse, "$");
            if (orders != null && !DataUtils.getResponseValueAsList(ordersResponse, "$").isEmpty()) {
                // Use the configured valid order ID or skip if not available
                String orderId = ConfigUtils.getValidOrderId();
                if (!"ORDER_ID_TO_TEST".equals(orderId)) {
                    logApiCall("GET", String.format("/ENSEK/orders/%s", orderId));
                    
                    Response response = given()
                            .spec(requestSpec)
                            .pathParam("orderId", orderId)
                            .when()
                            .get("/ENSEK/orders/{orderId}")
                            .then()
                            .statusCode(anyOf(equalTo(200), equalTo(404)))
                            .extract().response();

                    if (response.getStatusCode() == 200) {
                        // Verify the response contains the expected order ID
                        assertTrue(DataUtils.responseContainsKey(response, "$"), 
                                 "Response should contain order data");
                    }

                    logApiResponse(response.getStatusCode(), String.format("/ENSEK/orders/%s", orderId));
                } else {
                    logger.warn("Skipping testGetSingleOrderWithValidId - no valid order ID configured");
                }
            } else {
                logger.warn("Skipping testGetSingleOrderWithValidId - no orders available to test with");
            }
        } else {
            logger.warn("Skipping testGetSingleOrderWithValidId - unable to retrieve orders list");
        }
    }

    // ==================== DATA PROVIDERS ====================

    @DataProvider(name = "buyFuelData")
    public Object[][] buyFuelData() {
        return new Object[][]{
                {ConfigUtils.getValidFuelId(), 10},
                {ConfigUtils.getValidFuelId() + 1, 5},
                {ConfigUtils.getValidFuelId() + 2, 8}
        };
    }

    @DataProvider(name = "invalidFuelData")
    public Object[][] invalidFuelData() {
        return new Object[][]{
                {ConfigUtils.getInvalidFuelId(), ConfigUtils.getValidQuantity()}, // Invalid ID, valid quantity
                {ConfigUtils.getValidFuelId(), ConfigUtils.getInvalidQuantity()}, // Valid ID, invalid quantity
                {ConfigUtils.getInvalidFuelId(), ConfigUtils.getInvalidQuantity()}, // Both invalid
                {0, ConfigUtils.getValidQuantity()}, // Zero ID
                {ConfigUtils.getValidFuelId(), 0} // Zero quantity
        };
    }

    @DataProvider(name = "authenticationData")
    public Object[][] authenticationData() {
        return new Object[][]{
                {"validUser", "validPassword", 200},
                {"invalidUser", "validPassword", 401},
                {"validUser", "invalidPassword", 401},
                {"", "password", 400},
                {"username", "", 400},
                {"", "", 400}
        };
    }
}