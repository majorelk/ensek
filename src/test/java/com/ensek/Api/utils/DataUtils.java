package com.ensek.Api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Utility class for data manipulation and extraction from API responses
 */
public class DataUtils {

    private static final Logger logger = LoggerFactory.getLogger(DataUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    /**
     * Get response value as string using JsonPath
     */
    public static String getResponseValueAsString(Response response, String key) {
        try {
            return response.jsonPath().getString(key);
        } catch (Exception e) {
            logger.error("Failed to extract string value for key '{}' from response", key, e);
            return null;
        }
    }

    /**
     * Get response value as integer using JsonPath
     */
    public static Integer getResponseValueAsInt(Response response, String key) {
        try {
            return response.jsonPath().getInt(key);
        } catch (Exception e) {
            logger.error("Failed to extract integer value for key '{}' from response", key, e);
            return null;
        }
    }

    /**
     * Get response value as boolean using JsonPath
     */
    public static Boolean getResponseValueAsBoolean(Response response, String key) {
        try {
            return response.jsonPath().getBoolean(key);
        } catch (Exception e) {
            logger.error("Failed to extract boolean value for key '{}' from response", key, e);
            return null;
        }
    }

    /**
     * Get response value as list using JsonPath
     */
    public static <T> List<T> getResponseValueAsList(Response response, String key) {
        try {
            return response.jsonPath().getList(key);
        } catch (Exception e) {
            logger.error("Failed to extract list value for key '{}' from response", key, e);
            return null;
        }
    }

    /**
     * Check if response contains a specific key
     */
    public static boolean responseContainsKey(Response response, String key) {
        try {
            Object value = response.jsonPath().get(key);
            return value != null;
        } catch (Exception e) {
            logger.warn("Key '{}' not found in response", key);
            return false;
        }
    }

    /**
     * Get the full response body as a Map
     */
    public static Map<String, Object> getResponseAsMap(Response response) {
        try {
            return response.jsonPath().getMap("$");
        } catch (Exception e) {
            logger.error("Failed to convert response to Map", e);
            return null;
        }
    }

    /**
     * Parse JSON string to JsonNode
     */
    public static JsonNode parseJsonString(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (IOException e) {
            logger.error("Failed to parse JSON string", e);
            return null;
        }
    }

    /**
     * Convert object to JSON string
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Failed to convert object to JSON string", e);
            return null;
        }
    }

    /**
     * Generate random string of specified length
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generate random email address
     */
    public static String generateRandomEmail() {
        return "test." + generateRandomString(8) + "@example.com";
    }

    /**
     * Generate random password with specified length
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    /**
     * Find energy type by ID from energy list response
     */
    public static Map<String, Object> findEnergyTypeById(Response response, int energyId) {
        List<Map<String, Object>> energyTypes = getResponseValueAsList(response, "$");
        if (energyTypes != null) {
            for (Map<String, Object> energyType : energyTypes) {
                Integer id = (Integer) energyType.get("id");
                if (id != null && id == energyId) {
                    return energyType;
                }
            }
        }
        logger.warn("Energy type with ID {} not found in response", energyId);
        return null;
    }

    /**
     * Get available quantity for a specific energy type
     */
    public static Integer getAvailableQuantityForEnergyType(Response response, int energyId) {
        Map<String, Object> energyType = findEnergyTypeById(response, energyId);
        if (energyType != null) {
            return (Integer) energyType.get("quantity_available");
        }
        return null;
    }

    /**
     * Validate response structure contains required fields
     */
    public static boolean validateResponseStructure(Response response, String[] requiredFields) {
        for (String field : requiredFields) {
            if (!responseContainsKey(response, field)) {
                logger.error("Required field '{}' missing from response", field);
                return false;
            }
        }
        return true;
    }

    /**
     * Extract error message from response
     */
    public static String extractErrorMessage(Response response) {
        // Try common error message fields
        String[] errorFields = {"message", "error", "errorMessage", "detail", "details"};
        
        for (String field : errorFields) {
            String errorMessage = getResponseValueAsString(response, field);
            if (errorMessage != null && !errorMessage.trim().isEmpty()) {
                return errorMessage;
            }
        }
        
        // If no standard error field found, return the full response body
        return response.asString();
    }
}
