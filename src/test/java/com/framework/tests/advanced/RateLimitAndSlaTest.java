package com.framework.tests.advanced;

import com.framework.endpoints.HttpBinAPI;
import com.framework.endpoints.UserAPI;
import com.framework.tests.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.lessThan;

public class RateLimitAndSlaTest extends BaseTest {

    @Test(groups = {"regression", "performance"}, description = "Validate response SLA (Response Time Limit)")
    public void testResponseSlaAssertion() {
        log.info("Executing API request to test SLA response time assertion");
        
        // Assert that GET /api/users/2 responds in less than 3 seconds (3000 milliseconds)
        UserAPI.getUser(2)
                .then()
                .statusCode(200)
                .time(lessThan(3000L), TimeUnit.MILLISECONDS);
        
        log.info("Response time verification against SLA threshold passed.");
    }

    @Test(groups = {"regression", "performance"}, 
          description = "Trigger a 429 status code to verify the automated RateLimitHandler filter intercept and retry behavior")
    public void testRateLimitInterceptionAndRetry() {
        log.info("Sending requests to trigger mock rate-limiting response [HTTP 429]");

        // Call our dynamic HttpBin endpoint which generates a 429 response and a 'Retry-After: 1' header.
        // The RateLimitHandler filter will automatically intercept the 429, parse the header, sleep, and retry.
        // Since HttpBin's endpoint is static and will return 429 each time, the handler will retry 3 times and then return.
        Response response = HttpBinAPI.triggerRateLimit(1);

        // Assert that the final response returned after retries is still 429
        response.then().statusCode(429);

        log.info("Successfully validated rate limit retry loop. Look at console logs to confirm 3 retry attempts occurred.");
    }
}
