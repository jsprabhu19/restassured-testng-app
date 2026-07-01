package com.framework.endpoints;

import com.framework.api.BaseAPI;
import com.framework.pojo.UserPayload;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * Wrapper class containing endpoint utilities for managing User records on the ReqRes service.
 * Supports basic CRUD operations (POST, GET, PUT, DELETE) using standard specifications.
 */
public final class UserAPI {

    private static final Logger log = LoggerFactory.getLogger(UserAPI.class);
    private static final String USERS_ENDPOINT = "/users";
    private static final String USER_BY_ID_ENDPOINT = USERS_ENDPOINT + "/{id}";

    private UserAPI() {
        // Prevent instantiation
    }

    /**
     * Creates a new user record.
     *
     * @param payload User profile payload details
     * @return REST Assured Response
     */
    public static Response createUser(UserPayload payload) {
        log.info("Sending POST request to create user: {}", USERS_ENDPOINT);
        return given()
                .spec(BaseAPI.getReqResSpec())
                .body(payload)
                .when()
                .post(USERS_ENDPOINT);
    }

    /**
     * Retrieves a single user record by ID.
     *
     * @param id User ID to query
     * @return REST Assured Response
     */
    public static Response getUser(int id) {
        log.info("Sending GET request for user ID: {}", id);
        return given()
                .spec(BaseAPI.getReqResSpec())
                .pathParam("id", id) // Demonstrating path parameters
                .when()
                .get(USER_BY_ID_ENDPOINT);
    }

    /**
     * Retrieves a page of user records.
     *
     * @param page Target page number to retrieve
     * @return REST Assured Response
     */
    public static Response getUsers(int page) {
        log.info("Sending GET request for users list on page: {}", page);
        return given()
                .spec(BaseAPI.getReqResSpec())
                .queryParam("page", page) // Demonstrating query parameters
                .when()
                .get(USERS_ENDPOINT);
    }

    /**
     * Updates an existing user record.
     *
     * @param id User ID to update
     * @param payload Updated user profile details
     * @return REST Assured Response
     */
    public static Response updateUser(int id, UserPayload payload) {
        log.info("Sending PUT request to update user ID: {}", id);
        return given()
                .spec(BaseAPI.getReqResSpec())
                .pathParam("id", id)
                .body(payload)
                .when()
                .put(USER_BY_ID_ENDPOINT);
    }

    /**
     * Deletes an existing user record.
     *
     * @param id User ID to delete
     * @return REST Assured Response
     */
    public static Response deleteUser(int id) {
        log.info("Sending DELETE request for user ID: {}", id);
        return given()
                .spec(BaseAPI.getReqResSpec())
                .pathParam("id", id)
                .when()
                .delete(USER_BY_ID_ENDPOINT);
    }
}
