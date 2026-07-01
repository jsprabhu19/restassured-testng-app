package com.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.framework.config.FrameworkConstants;

/**
 * Extent Report manager utility for configuring, writing, and accessing test metrics.
 * Utilizes a ThreadLocal pattern to ensure thread safety across parallel test execution runs.
 */
public final class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private ExtentReportManager() {
        // Prevent instantiation
    }

    /**
     * Initializes the report instance and maps ExtentSparkReporter configuration.
     * Invoked once at suite level startup.
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
     * Invoked once at suite completion.
     */
    public static synchronized void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    /**
     * Creates a test instance in the extent system and attaches it to the current running thread.
     *
     * @param testName Name of the test suite method
     */
    public static void createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
    }

    /**
     * Retrieves the ExtentTest instance bound to the executing thread context.
     *
     * @return ExtentTest thread local instance
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Unloads the thread-local instance after the test completes.
     * Prevents thread-local memory leakages.
     */
    public static void unload() {
        extentTest.remove();
    }

    /**
     * Logs a successful test validation message to the current test run.
     *
     * @param message Validation description
     */
    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().pass(message);
        }
    }

    /**
     * Logs a failed validation description along with stack trace details.
     *
     * @param message Failure explanation
     * @param t Caught throwable/assertion stack details
     */
    public static void logFail(String message, Throwable t) {
        if (getTest() != null) {
            getTest().fail(message).fail(t);
        }
    }

    /**
     * Logs an informational status block into the report output.
     *
     * @param message Status detail description
     */
    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().info(message);
        }
    }
}
