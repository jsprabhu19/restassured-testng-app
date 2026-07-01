package com.framework.api;

import com.framework.config.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Class for managing REST Assured RequestSpecifications.
 * Instantiates pre-configured request blueprints with custom timeout
 * parameters, SSL validations,
 * request/response logging filters, and rate-limiting retry policies.
 */
public class BaseAPI {

    private static final Logger log = LoggerFactory.getLogger(BaseAPI.class);

    private BaseAPI() {
        // Prevent instantiation
    }

    /**
     * Builds a thread-safe, pre-configured RequestSpecification for a specific base
     * URI.
     * Sets common headers, timeouts, SSL validation bypass, custom masking filters,
     * and rate-limiting retry filters.
     *
     * @param baseUri Target Base URL
     * @return RequestSpecification
     */
    public static RequestSpecification getBaseSpec(String baseUri) {
        log.info("Constructing new RequestSpecification for Base URI: {}", baseUri);

        ConfigReader reader = ConfigReader.getInstance();
        int connTimeout = reader.getInt("timeout.connection");
        int socketTimeout = reader.getInt("timeout.socket");

        // Custom HttpClient configuration to set socket and connection timeouts
        RestAssuredConfig timeoutConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", connTimeout)
                        .setParam("http.socket.timeout", socketTimeout));

        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(timeoutConfig)
                .setRelaxedHTTPSValidation() // Bypass SSL/TLS errors (common interview scenario)
                .addFilter(new LogMaskFilter()) // Mask sensitive fields in logs
                .addFilter(new RateLimitHandler()) // Automatically handle 429 retries
                .build();
    }

    /**
     * Helper to retrieve a standard RequestSpecification for the ReqRes API.
     */
    public static RequestSpecification getReqResSpec() {
        return getBaseSpec(ConfigReader.getInstance().getBaseUriReqRes());
    }

    /**
     * Helper to retrieve a standard RequestSpecification for the HttpBin API.
     */
    public static RequestSpecification getHttpBinSpec() {
        return getBaseSpec(ConfigReader.getInstance().getBaseUriHttpBin());
    }
}
