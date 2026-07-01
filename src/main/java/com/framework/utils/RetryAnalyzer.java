package com.framework.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestNG Retry Analyzer implementation.
 * Automatically retries failed tests up to a maximum limit with a 2000 ms backoff delay
 * between attempts to allow transient network or service rate limit windows to recover.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int MAX_RETRY_LIMIT = 2; // Will retry a failed test up to 2 times

    /**
     * Determines whether a failed test should be retried.
     * Inserts a backoff delay before notifying TestNG to execute the retry.
     *
     * @param result Test result details of the failed method run
     * @return True if retry limit not exceeded, causing TestNG to re-run the method. False otherwise.
     */
    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (retryCount < MAX_RETRY_LIMIT) {
                retryCount++;
                log.warn("Test '{}' failed. Retrying execution (Attempt {}/{})...", 
                        result.getName(), retryCount, MAX_RETRY_LIMIT);
                try {
                    log.info("Sleeping 2000 ms before retry attempt {} for test '{}' to allow network/API recovery...", 
                            retryCount, result.getName());
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return true; // Tells TestNG to re-run the test case
            }
        }
        return false;
    }
}
