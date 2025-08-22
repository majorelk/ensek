package com.ensek.Api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for test reporting and logging
 */
public class TestReportUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(TestReportUtils.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void logTestStatus(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String status = getStatusString(result.getStatus());
        String timestamp = dateFormat.format(new Date(result.getEndMillis()));
        long duration = result.getEndMillis() - result.getStartMillis();
        
        String logMessage = String.format("Test: %s | Status: %s | Duration: %dms | Completed: %s",
                testName, status, duration, timestamp);
        
        System.out.println(logMessage);
        logger.info(logMessage);
        
        // Log failure details if test failed
        if (result.getStatus() == ITestResult.FAILURE && result.getThrowable() != null) {
            logger.error("Failure details for test {}: {}", testName, result.getThrowable().getMessage());
        }
    }
    
    public static void logTestSuiteStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        String testName = context.getName();
        logger.info("Starting test suite: {} - Test: {}", suiteName, testName);
        System.out.println("=== Starting Test Suite: " + suiteName + " - Test: " + testName + " ===");
    }
    
    public static void logTestSuiteEnd(ITestContext context) {
        String suiteName = context.getSuite().getName();
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;
        
        String summary = String.format("Test Suite: %s - Total: %d, Passed: %d, Failed: %d, Skipped: %d",
                suiteName, total, passed, failed, skipped);
        
        logger.info(summary);
        System.out.println("=== " + summary + " ===");
    }
    
    public static void logApiRequest(String method, String endpoint, int statusCode) {
        String logMessage = String.format("API Request: %s %s -> Status: %d", method, endpoint, statusCode);
        logger.info(logMessage);
    }
    
    public static void logApiRequest(String method, String endpoint, String body, int statusCode) {
        String logMessage = String.format("API Request: %s %s | Body: %s -> Status: %d", 
                method, endpoint, body, statusCode);
        logger.info(logMessage);
    }
    
    private static String getStatusString(int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                return "PASSED";
            case ITestResult.FAILURE:
                return "FAILED";
            case ITestResult.SKIP:
                return "SKIPPED";
            case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
                return "PARTIAL_SUCCESS";
            default:
                return "UNKNOWN";
        }
    }
    
    public static void generateCustomReport(ITestContext context) {
        logger.info("Generating custom test report...");
        // Implementation for custom reporting can be added here
        // This could generate HTML reports, send notifications, etc.
    }
}
