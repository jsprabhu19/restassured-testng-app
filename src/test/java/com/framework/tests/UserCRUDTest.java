package com.framework.tests;

import com.framework.endpoints.UserAPI;
import com.framework.pojo.UserPayload;
import com.framework.pojo.UserResponse;
import com.framework.utils.DataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserCRUDTest extends BaseTest {

    private int createdUserId;
    private String generatedName;
    private String generatedJob;

    @Test(groups = {"smoke", "regression"}, description = "Create User - POST Operation")
    public void testCreateUser() {
        generatedName = DataGenerator.getFullName();
        generatedJob = DataGenerator.getJobTitle();

        UserPayload payload = UserPayload.builder()
                .name(generatedName)
                .job(generatedJob)
                .build();

        Response response = UserAPI.createUser(payload);

        // Verify status code
        response.then().statusCode(201);

        // Deserialize response to verify response payload structure
        UserResponse userResponse = response.as(UserResponse.class);
        
        assertThat(userResponse.getId()).isGreaterThan(0);
        assertThat(userResponse.getName()).isEqualTo(generatedName);
        assertThat(userResponse.getJob()).isEqualTo(generatedJob);

        createdUserId = userResponse.getId();
        log.info("Successfully created user with dynamic ID: {}", createdUserId);
    }

    @Test(dependsOnMethods = "testCreateUser", groups = {"regression"}, description = "Read User - GET Operation using path parameter dependency")
    public void testGetCreatedUser() {
        // NOTE: In mock APIs like JSONPlaceholder, users created via POST are not permanently persisted.
        // Thus, querying the newly created ID will return 404. We demonstrate passing the ID,
        // but validate against a known static user (ID = 2) to ensure GET schema and data matching works.
        log.info("Simulating query for dynamically created user ID: {}", createdUserId);
        Response mockGetResponse = UserAPI.getUser(createdUserId);
        log.info("Mock GET request status code for non-persisted user: {}", mockGetResponse.getStatusCode());

        // Validate GET on an active record (User ID = 2)
        Response activeUserResponse = UserAPI.getUser(2);
        activeUserResponse.then().statusCode(200);

        UserResponse userResponse = activeUserResponse.as(UserResponse.class);
        assertThat(userResponse.getId()).isEqualTo(2);
        assertThat(userResponse.getName()).isNotEmpty();
        assertThat(userResponse.getEmail()).contains("@");
    }

    @Test(dependsOnMethods = "testCreateUser", groups = {"regression"}, description = "Update User - PUT Operation")
    public void testUpdateUser() {
        String updatedJob = generatedJob + " - Updated";
        
        UserPayload payload = UserPayload.builder()
                .name(generatedName)
                .job(updatedJob)
                .build();

        // Pass a valid user ID (e.g. 2) to ensure request succeeds on JSONPlaceholder
        Response response = UserAPI.updateUser(2, payload);
        response.then().statusCode(200);

        UserResponse userResponse = response.as(UserResponse.class);
        assertThat(userResponse.getJob()).isEqualTo(updatedJob);
        log.info("Successfully updated job profile for user ID: {}", 2);
    }

    @Test(dependsOnMethods = "testCreateUser", groups = {"smoke", "regression"}, description = "Delete User - DELETE Operation")
    public void testDeleteUser() {
        // Pass a valid user ID (e.g. 2) to ensure request succeeds on JSONPlaceholder
        Response response = UserAPI.deleteUser(2);
        // JSONPlaceholder returns 200 OK for successful delete operations
        response.then().statusCode(200);
        log.info("Successfully deleted mock user ID: {}", 2);
    }
}
