package com.framework.utils;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom TestNG Listener that hooks into test execution lifecycle events (Suite, Test, and Method).
 * Responsible for initializing ExtentReports, updating status logs, reporting pass/fail details,
 * and displaying thread-specific console logs to assist in parallel execution analysis.
 */
@SuppressWarnings("deprecation")
public class FrameworkListener implements ISuiteListener, ITestListener, IInvokedMethodListener {

    private static final Logger log = LoggerFactory.getLogger(FrameworkListener.class);

    // ==================== ISuiteListener ====================

    /**
     * Triggered before suite execution starts. Initializes Extent Report configuration.
     *
     * @param suite TestNG Suite context
     */
    @Override
    public void onStart(ISuite suite) {
        log.info("Suite execution started: {}", suite.getName());
        ExtentReportManager.initReports();
    }

    /**
     * Triggered after suite execution finishes. Writes metrics into the HTML report.
     *
     * @param suite TestNG Suite context
     */
    @Override
    public void onFinish(ISuite suite) {
        log.info("Suite execution finished: {}", suite.getName());
        ExtentReportManager.flushReports();
    }

    // ==================== ITestListener ====================

    /**
     * Triggered on starting any test method. Instantiates Extent test container.
     *
     * @param result Method result details
     */
    @Override
    public void onTestStart(ITestResult result) {
        log.info("Starting test method: {}", result.getMethod().getMethodName());
        ExtentReportManager.createTest(result.getMethod().getMethodName());
        ExtentReportManager.logInfo("Test Case [" + result.getMethod().getMethodName() + "] has started.");
    }

    /**
     * Triggered when a test method successfully completes all assertions.
     *
     * @param result Method result details
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Test passed: {}", result.getMethod().getMethodName());
        ExtentReportManager.logPass("Test Case [" + result.getMethod().getMethodName() + "] passed.");
        ExtentReportManager.unload();
    }

    /**
     * Triggered when a test method throws an assertion failure or uncaught exception.
     *
     * @param result Method result details
     */
    @Override
    public void onTestFailure(ITestResult result) {
        log.error("Test failed: {}", result.getMethod().getMethodName(), result.getThrowable());
        ExtentReportManager.logFail("Test Case [" + result.getMethod().getMethodName() + "] failed.", result.getThrowable());
        ExtentReportManager.unload();
    }

    /**
     * Triggered when a test method execution is skipped due to configuration failure or missing dependency.
     *
     * @param result Method result details
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("Test skipped: {}", result.getMethod().getMethodName());
        ExtentReportManager.logInfo("Test Case [" + result.getMethod().getMethodName() + "] was skipped.");
        ExtentReportManager.unload();
    }

    // ==================== IInvokedMethodListener ====================

    /**
     * Triggered before any test method invocation. Logs thread ID and name details.
     *
     * @param method Invoked method reference
     * @param testResult Method execution context
     */
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            log.info("==> [INVOCATION START] Executing method '{}' on Thread ID: {} / Name: {}",
                    method.getTestMethod().getMethodName(), 
                    Thread.currentThread().getId(),
                    Thread.currentThread().getName());
        }
    }

    /**
     * Triggered after any test method completes. Logs thread completion context.
     *
     * @param method Invoked method reference
     * @param testResult Method execution context
     */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            log.info("<== [INVOCATION END] Completed method '{}' on Thread ID: {} / Name: {}",
                    method.getTestMethod().getMethodName(), 
                    Thread.currentThread().getId(),
                    Thread.currentThread().getName());
        }
    }
}
