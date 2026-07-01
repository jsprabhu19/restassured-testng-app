package com.framework.endpoints;

import com.framework.api.BaseAPI;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static io.restassured.RestAssured.given;

/**
 * Wrapper class containing endpoint utilities for the HttpBin sandbox service.
 * Supports testing multipart file uploads, binary stream downloads, and simulated rate-limiting responses.
 */
public final class HttpBinAPI {

    private static final Logger log = LoggerFactory.getLogger(HttpBinAPI.class);

    private HttpBinAPI() {
        // Prevent instantiation
    }

    /**
     * Uploads a file using multipart form-data to HttpBin.
     * Demonstrates RestAssured's multiPart file uploading.
     *
     * @param file Target File to upload
     * @return REST Assured Response
     */
    public static Response uploadFile(File file) {
        log.info("Uploading file '{}' using multiPart form-data to HttpBin.", file.getName());
        return given()
                .spec(BaseAPI.getHttpBinSpec())
                .contentType("multipart/form-data") // Override default JSON content type
                .multiPart("file", file)
                .when()
                .post("/post");
    }

    /**
     * Downloads a stream of random binary bytes from HttpBin.
     * Demonstrates handling binary file downloads.
     *
     * @param numBytes Number of random bytes to generate and stream
     * @return REST Assured Response
     */
    public static Response downloadBytes(int numBytes) {
        log.info("Downloading binary data stream of size {} bytes.", numBytes);
        return given()
                .spec(BaseAPI.getHttpBinSpec())
                .pathParam("num", numBytes)
                .when()
                .get("/bytes/{num}");
    }

    /**
     * Triggers a mock Rate Limit (429) status code on HttpBin.
     * Supplies a custom Retry-After header to test the automated RateLimitHandler filter.
     *
     * @param retryAfterSecs Time in seconds to hold backoff
     * @return REST Assured Response
     */
    public static Response triggerRateLimit(int retryAfterSecs) {
        log.info("Triggering mock 429 Rate Limit on HttpBin");
        return given()
                .spec(BaseAPI.getHttpBinSpec())
                .when()
                .get("/status/429");
    }
}
