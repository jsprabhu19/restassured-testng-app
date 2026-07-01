package com.framework.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int MAX_RETRY_LIMIT = 2; // Will retry a failed test up to 2 times

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (retryCount < MAX_RETRY_LIMIT) {
                retryCount++;
                log.warn("Test '{}' failed. Retrying execution (Attempt {}/{})...", 
                        result.getName(), retryCount, MAX_RETRY_LIMIT);
                return true; // Tells TestNG to re-run the test case
            }
        }
        return false;
    }
}
