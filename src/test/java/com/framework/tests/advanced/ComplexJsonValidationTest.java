package com.framework.tests.advanced;

import com.framework.api.BaseAPI;
import com.framework.config.FrameworkConstants;
import com.framework.endpoints.UserAPI;
import com.framework.tests.BaseTest;
import com.framework.utils.JSONCompareUtil;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class ComplexJsonValidationTest extends BaseTest {

    @Test(groups = {"regression", "assertions"}, description = "Demonstrate deep JSON querying using Groovy GPath expressions")
    public void testComplexJsonGPathValidations() {
        // Query users
        Response response = UserAPI.getUsers(2);
        response.then().statusCode(200);

        // 1. Validate size and presence using Hamcrest Matchers directly
        response.then()
                .body("$", hasSize(10))
                .body("id", hasItems(1, 2, 3, 4, 5))
                .body("email", hasItem("Sincere@april.biz"));

        // 2. Perform advanced queries using Groovy GPath expressions on the JSON Path
        // Extract all email addresses
        List<String> emails = response.jsonPath().getList("collect { it.email }");
        log.info("Extracted emails: {}", emails);
        
        // Find single user matching criteria
        Map<String, ?> ervinUser = response.jsonPath().getMap("find { it.name == 'Ervin Howell' }");
        log.info("Ervin Howell's User Details: {}", ervinUser);
        
        // Validate matching details
        response.then().body("find { it.name == 'Ervin Howell' }.email", equalTo("Shanna@melissa.tv"));

        // Find all users with ID > 8
        List<Map<String, ?>> matchingUsers = response.jsonPath().getList("findAll { it.id > 8 }");
        log.info("Users with ID > 8: {}", matchingUsers);
        response.then().body("findAll { it.id > 8 }.id", containsInAnyOrder(9, 10));

        // Find the user with maximum ID
        int maxId = response.jsonPath().getInt("max { it.id }.id");
        log.info("Max user ID: {}", maxId);
        response.then().body("max { it.id }.name", equalTo("Clementina DuBuque"));
    }

    @Test(groups = {"regression", "assertions"}, description = "Validate actual JSON response against an expected JSON reference template file")
    public void testActualVsExpectedJsonTree() {
        // GET standard User 2 response
        Response response = UserAPI.getUser(2);
        response.then().statusCode(200);

        // Path to the expected reference JSON file
        String expectedFilePath = FrameworkConstants.RESOURCES_PATH + File.separator + "expected-user.json";

        // Assert JSON structures are equal (using Lenient mode so matching is flexible to extra fields/order)
        JSONCompareUtil.assertJsonFileEquality(expectedFilePath, response.getBody().asString(), false);
    }

    @Test(groups = {"regression", "assertions"}, description = "Compare RestAssured then-chaining style with decoupled SoftAssert validations")
    public void testThenChainingVsDecoupled() {
        // Style A: RestAssured Fluent Chaining (Excellent for simple/quick validations)
        log.info("Executing fluent chaining assertion style");
        UserAPI.getUser(2)
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", notNullValue())
                .body("email", containsString("@"));

        // Style B: Decoupled validation using TestNG SoftAssert (Perfect for keeping test logs distinct and clean)
        log.info("Executing decoupled SoftAssert assertion style");
        Response response = UserAPI.getUser(2);

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(response.getStatusCode(), 200, "Verify status code is 200");
        soft.assertEquals(response.jsonPath().getInt("id"), 2, "Verify user ID is 2");
        soft.assertNotNull(response.jsonPath().getString("name"), "Verify name is not null");
        soft.assertTrue(response.jsonPath().getString("email").contains("@"), "Verify email matches format");

        // Asserts and logs all failures if any occurred
        soft.assertAll();
    }

    @Test(groups = {"regression", "schema"}, description = "Assert API contract compliance via JSON Schema validation")
    public void testJsonSchemaValidation() {
        // Query user and match against JSON Schema
        given()
                .spec(BaseAPI.getReqResSpec())
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
        log.info("JSON Schema validation passed successfully!");
    }
}
