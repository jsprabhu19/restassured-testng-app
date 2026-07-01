package com.framework.api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom REST Assured Filter that automatically detects HTTP 429 Too Many Requests responses
 * and handles rate limiting by performing thread sleep backoff based on the 'Retry-After'
 * header (or falling back to a default backoff delay).
 */
public class RateLimitHandler implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitHandler.class);
    private static final int DEFAULT_BACKOFF_MS = 1000;

    /**
     * Intercepts HTTP response to detect 429 Rate Limit error. If found, retrieves the
     * "Retry-After" header, calculates backoff duration, holds execution, and then resumes.
     *
     * @param requestSpec Request specification details
     * @param responseSpec Response specification details
     * @param ctx RestAssured filter context
     * @return HTTP Response object
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);

        if (response != null && response.getStatusCode() == 429) {
            String retryAfterHeader = response.getHeader("Retry-After");
            long waitTimeMs = DEFAULT_BACKOFF_MS;

            if (retryAfterHeader != null && !retryAfterHeader.trim().isEmpty()) {
                try {
                    // Retry-After might specify seconds to wait
                    waitTimeMs = Long.parseLong(retryAfterHeader.trim()) * 1000L;
                } catch (NumberFormatException e) {
                    log.warn("Non-integer Retry-After header: '{}'. Using default backoff of {} ms.", retryAfterHeader, waitTimeMs);
                }
            }

            log.warn("Rate Limit [HTTP 429] detected. Automatically holding thread execution for {} ms backoff...", waitTimeMs);
            
            try {
                Thread.sleep(waitTimeMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Rate limit retry backoff sleep interrupted", e);
            }
        }

        return response;
    }
}
