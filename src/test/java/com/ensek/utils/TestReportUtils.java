package com.ensek.utils;

import org.testng.ITestContext;
import org.testng.ITestResult;

public class TestReportUtils {

    public static void logTestStatus(ITestResult result) {
        System.out.println("Test: " + result.getMethod().getMethodName() + " - Status: " + result.getStatus());
    }

    // Add more custom reporting methods as needed
}
