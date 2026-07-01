package com.framework.tests;

import com.framework.config.FrameworkConstants;
import com.framework.endpoints.UserAPI;
import com.framework.pojo.UserPayload;
import com.framework.pojo.UserResponse;
import com.framework.utils.JSONReader;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Suite validating parallel data-driven execution.
 * Reads inputs from static JSON files and runs creation tests concurrently.
 */
public class DataDrivenUserTest extends BaseTest {

    /**
     * TestNG DataProvider configured with parallel = true.
     * Instructs the execution engine to run test records simultaneously on separate threads,
     * demonstrating framework thread-safety.
     */
    @DataProvider(name = "userDataProvider", parallel = true)
    public Object[][] getUserData() {
        List<Map<String, String>> testDataList = JSONReader.readTestData(FrameworkConstants.TEST_DATA_PATH);
        Object[][] data = new Object[testDataList.size()][1];
        
        for (int i = 0; i < testDataList.size(); i++) {
            data[i][0] = testDataList.get(i);
        }
        return data;
    }

    @Test(dataProvider = "userDataProvider", groups = {"regression", "datadriven"}, 
          description = "Verify concurrent user creation using parallel DataProvider")
    public void testCreateUserMultiple(Map<String, String> testData) {
        String name = testData.get("name");
        String job = testData.get("job");

        log.info("==> Running parallel user creation for [{}, {}] on Thread: {}", 
                name, job, Thread.currentThread().getName());

        UserPayload payload = UserPayload.builder()
                .name(name)
                .job(job)
                .build();

        Response response = UserAPI.createUser(payload);

        // Verify status code
        response.then().statusCode(201);

        // Deserialize response and assert values
        UserResponse responseBody = response.as(UserResponse.class);
        assertThat(responseBody.getName()).isEqualTo(name);
        assertThat(responseBody.getJob()).isEqualTo(job);

        log.info("<== Completed creation for [{}, {}] on Thread: {}", 
                name, job, Thread.currentThread().getName());
    }
}
