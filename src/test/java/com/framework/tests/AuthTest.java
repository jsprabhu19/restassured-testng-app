package com.framework.tests;

import com.framework.endpoints.AuthAPI;
import com.framework.pojo.LoginPayload;
import com.framework.api.BaseAPI;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class AuthTest extends BaseTest {

    private String extractedToken;

    @Test(groups = {"smoke", "auth"}, description = "Verify user login and token extraction")
    public void testLoginAndExtractToken() {
        // Prepare login credentials (standard ReqRes credentials)
        LoginPayload payload = LoginPayload.builder()
                .email("eve.holt@reqres.in")
                .password("cityslicka")
                .build();

        Response response = AuthAPI.login(payload);

        // Verify status code
        response.then().statusCode(200);

        // Extract token from HttpBin post response echo
        extractedToken = response.jsonPath().getString("json.password");

        log.info("Successfully extracted login token: {}", extractedToken);
        assertThat(extractedToken).isNotEmpty();
    }

    @Test(dependsOnMethods = "testLoginAndExtractToken", groups = {"auth"}, description = "Demonstrate passing Bearer token to a secured endpoint")
    public void testSecuredRouteWithBearerToken() {
        log.info("Sending Bearer token: {} to secure endpoint /headers on HttpBin", extractedToken);
        
        Response response = given()
                .spec(BaseAPI.getHttpBinSpec())
                .header("Authorization", "Bearer " + extractedToken)
                .when()
                .get("/headers");

        // Verify authentication success. Support both String value (Python httpbin) and Array/List (Go httpbin)
        response.then()
                .statusCode(200)
                .body("headers.Authorization", anyOf(
                        equalTo("Bearer " + extractedToken),
                        hasItem("Bearer " + extractedToken)
                ));
    }

    @Test(groups = {"auth"}, description = "Demonstrate standard Basic Authentication")
    public void testStandardBasicAuth() {
        // HttpBin endpoint /basic-auth/{user}/{passwd} validates credentials
        Response response = given()
                .spec(BaseAPI.getHttpBinSpec())
                .auth()
                .basic("admin", "adminPassword")
                .when()
                .get("/basic-auth/admin/adminPassword");

        response.then()
                .statusCode(200)
                .body("user", equalTo("admin"));

        // Support both "authenticated: true" (Python/recent Go httpbin) and "authorized: true" (older Go httpbin)
        Object authenticatedVal = response.jsonPath().get("authenticated");
        Object authorizedVal = response.jsonPath().get("authorized");
        boolean isAuth = Boolean.TRUE.equals(authenticatedVal) || Boolean.TRUE.equals(authorizedVal);
        assertThat(isAuth).isTrue();
    }

    @Test(groups = {"auth"}, description = "Demonstrate Preemptive Basic Authentication")
    public void testPreemptiveBasicAuth() {
        // Preemptive auth sends credentials directly in request header without waiting for 401 challenge
        Response response = given()
                .spec(BaseAPI.getHttpBinSpec())
                .auth()
                .preemptive()
                .basic("admin", "adminPassword")
                .when()
                .get("/basic-auth/admin/adminPassword");

        response.then()
                .statusCode(200)
                .body("user", equalTo("admin"));

        // Support both "authenticated: true" and "authorized: true"
        Object authenticatedVal = response.jsonPath().get("authenticated");
        Object authorizedVal = response.jsonPath().get("authorized");
        boolean isAuth = Boolean.TRUE.equals(authenticatedVal) || Boolean.TRUE.equals(authorizedVal);
        assertThat(isAuth).isTrue();
    }
}
