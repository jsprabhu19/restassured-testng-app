package com.framework.endpoints;

import com.framework.api.BaseAPI;
import com.framework.pojo.LoginPayload;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * API Wrappers for handling user authentication and registration requests.
 * Uses the HttpBin specification to echo payload values to simulate token responses.
 */
public final class AuthAPI {

    private static final Logger log = LoggerFactory.getLogger(AuthAPI.class);
    private static final String LOGIN_ENDPOINT = "/post";
    private static final String REGISTER_ENDPOINT = "/post";

    private AuthAPI() {
        // Prevent instantiation
    }

    /**
     * Executes login request returning the raw API response.
     *
     * @param payload Login credential payload
     * @return REST Assured Response
     */
    public static Response login(LoginPayload payload) {
        log.info("Sending POST request to login: {}", LOGIN_ENDPOINT);
        return given()
                .spec(BaseAPI.getHttpBinSpec())
                .body(payload)
                .when()
                .post(LOGIN_ENDPOINT);
    }

    /**
     * Executes registration request returning the raw API response.
     *
     * @param payload Registration payload details
     * @return REST Assured Response
     */
    public static Response register(LoginPayload payload) {
        log.info("Sending POST request to register user: {}", REGISTER_ENDPOINT);
        return given()
                .spec(BaseAPI.getHttpBinSpec())
                .body(payload)
                .when()
                .post(REGISTER_ENDPOINT);
    }
}
