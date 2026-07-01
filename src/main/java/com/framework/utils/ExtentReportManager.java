package com.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.framework.config.FrameworkConstants;

public final class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private ExtentReportManager() {
        // Prevent instantiation
    }

    /**
     * Initializes the report instance and maps ExtentSparkReporter config.
     */
    public static synchronized void initReports() {
        if (extent == null) {
            extent = new ExtentReports();
            ExtentSparkReporter spark = new ExtentSparkReporter(FrameworkConstants.REPORTS_PATH);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("API Test Automation Framework Execution Report");
            spark.config().setReportName("REST Assured + TestNG Integration Run");
            extent.attachReporter(spark);
        }
    }

    /**
     * Writes all test runs out to the HTML report.
     */
    public static synchronized void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    /**
     * Creates a test instance in the extent system and attaches it to the current running thread.
     */
    public static void createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Unloads the thread-local instance after the test completes.
     */
    public static void unload() {
        extentTest.remove();
    }

    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().pass(message);
        }
    }

    public static void logFail(String message, Throwable t) {
        if (getTest() != null) {
            getTest().fail(message).fail(t);
        }
    }

    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().info(message);
        }
    }
}
