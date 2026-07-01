package com.framework.api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimitHandler implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitHandler.class);
    private static final int DEFAULT_BACKOFF_MS = 1000;

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
