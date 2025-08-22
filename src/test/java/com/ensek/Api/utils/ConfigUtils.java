package com.ensek.Api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading configuration properties
 */
public class ConfigUtils {
    
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConfigUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE, ex);
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }
    
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
    
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
    
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    // Specific getters for common configuration values
    public static String getBaseUrl() {
        return getProperty("base.url");
    }
    
    public static String getBasePath() {
        return getProperty("base.path");
    }
    
    public static String getAuthToken() {
        return getProperty("auth.token");
    }
    
    public static String getTestUsername() {
        return getProperty("test.username");
    }
    
    public static String getTestPassword() {
        return getProperty("test.password");
    }
    
    public static int getValidFuelId() {
        return getIntProperty("fuel.id.valid");
    }
    
    public static int getInvalidFuelId() {
        return getIntProperty("fuel.id.invalid");
    }
    
    public static int getOutOfStockFuelId() {
        return getIntProperty("fuel.id.out.of.stock");
    }
    
    public static int getTestFuelId() {
        return getIntProperty("fuel.id.test");
    }
    
    public static int getValidQuantity() {
        return getIntProperty("quantity.valid");
    }
    
    public static int getInvalidQuantity() {
        return getIntProperty("quantity.invalid");
    }
    
    public static int getZeroQuantity() {
        return getIntProperty("quantity.zero");
    }
    
    public static int getExcessQuantity() {
        return getIntProperty("quantity.excess");
    }
    
    public static String getValidOrderId() {
        return getProperty("order.id.valid");
    }
    
    public static String getInvalidOrderId() {
        return getProperty("order.id.invalid");
    }
    
    public static int getDefaultTimeout() {
        return getIntProperty("timeout.default");
    }
    
    public static int getLongTimeout() {
        return getIntProperty("timeout.long");
    }
    
    public static int getMaxRetryAttempts() {
        return getIntProperty("retry.max.attempts");
    }
    
    public static int getRetryDelaySeconds() {
        return getIntProperty("retry.delay.seconds");
    }
}