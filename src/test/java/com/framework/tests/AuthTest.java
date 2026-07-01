package com.framework.tests;

import com.framework.endpoints.AuthAPI;
import com.framework.pojo.LoginPayload;
import com.framework.pojo.LoginResponse;
import com.framework.api.BaseAPI;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
        log.info("Sending Bearer token: {} to secure endpoint /bearer on HttpBin", extractedToken);
        
        Response response = given()
                .spec(BaseAPI.getHttpBinSpec())
                .header("Authorization", "Bearer " + extractedToken)
                .when()
                .get("/bearer");

        // Verify authentication success
        response.then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .body("token", equalTo(extractedToken));
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
                .body("authenticated", equalTo(true))
                .body("user", equalTo("admin"));
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
                .body("authenticated", equalTo(true))
                .body("user", equalTo("admin"));
    }
}
