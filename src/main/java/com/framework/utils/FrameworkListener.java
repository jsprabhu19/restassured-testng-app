package com.framework.utils;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class FrameworkListener implements ISuiteListener, ITestListener, IInvokedMethodListener {

    private static final Logger log = LoggerFactory.getLogger(FrameworkListener.class);

    // ==================== ISuiteListener ====================

    @Override
    public void onStart(ISuite suite) {
        log.info("Suite execution started: {}", suite.getName());
        ExtentReportManager.initReports();
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("Suite execution finished: {}", suite.getName());
        ExtentReportManager.flushReports();
    }

    // ==================== ITestListener ====================

    @Override
    public void onTestStart(ITestResult result) {
        log.info("Starting test method: {}", result.getMethod().getMethodName());
        ExtentReportManager.createTest(result.getMethod().getMethodName());
        ExtentReportManager.logInfo("Test Case [" + result.getMethod().getMethodName() + "] has started.");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Test passed: {}", result.getMethod().getMethodName());
        ExtentReportManager.logPass("Test Case [" + result.getMethod().getMethodName() + "] passed.");
        ExtentReportManager.unload();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("Test failed: {}", result.getMethod().getMethodName(), result.getThrowable());
        ExtentReportManager.logFail("Test Case [" + result.getMethod().getMethodName() + "] failed.", result.getThrowable());
        ExtentReportManager.unload();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("Test skipped: {}", result.getMethod().getMethodName());
        ExtentReportManager.logInfo("Test Case [" + result.getMethod().getMethodName() + "] was skipped.");
        ExtentReportManager.unload();
    }

    // ==================== IInvokedMethodListener ====================

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            log.info("==> [INVOCATION START] Executing method '{}' on Thread ID: {} / Name: {}",
                    method.getTestMethod().getMethodName(), 
                    Thread.currentThread().getId(),
                    Thread.currentThread().getName());
        }
    }

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
