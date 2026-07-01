# Implementation Plan: RestAssured & TestNG API Automation Framework (Advanced Interview Guide)

This implementation plan details a production-grade, decoupled, and thread-safe API automation framework using RestAssured and TestNG. It integrates advanced API design patterns and custom validation techniques to serve as an ultimate reference and interview-readiness manual.

---

## User Review Required

> [!IMPORTANT]
> - **Java Version**: We will write the framework using **Java 17+** features, ensuring compatibility with your local **Java 25** system and **Maven 3.3.9**.
> - **Mock APIs**:
>   - **ReqRes**: Used for standard CRUD and Authentication.
>   - **HttpBin**: Used for file upload/download, rate limiting simulation (`/status/429`), and response SLA verification.
> - **Libraries & Dependencies**:
>   - We will add **JSONAssert** (`org.skyscreamer:jsonassert`) for actual vs expected JSON comparison.
>   - We will add **JavaFaker** for dynamic data generation.
>   - We will add **ExtentReports** with thread-safe support.

---

## Open Questions

> [!NOTE]
> None. The prompt is fully specified and incorporates all requested coverage points.

---

## Proposed Changes

### Project Build Configuration

#### [NEW] [pom.xml](file:///e:/Learning/antigravity-projects/restassured-testng-app/pom.xml)
Configures Maven dependencies for:
* **RestAssured** (`io.rest-assured:rest-assured`, `io.rest-assured:json-schema-validator`)
* **TestNG** (`org.testng:testng`)
* **Lombok** (`org.projectlombok:lombok`)
* **Jackson Databind** (`com.fasterxml.jackson.core:jackson-databind`)
* **JSONAssert** (`org.skyscreamer:jsonassert`) for expected vs actual JSON tree matching.
* **JavaFaker** (`com.github.javafaker:javafaker`) for dynamic test data.
* **ExtentReports** (`com.aventstack:extentreports`)
* **AssertJ** (`org.assertj:assertj-core`)
* **Log4j2** (`org.apache.logging.log4j:log4j-slf4j2-impl`) for structured logs.
* **Maven Surefire Plugin** set up for parallel execution using `testng.xml`.

---

### Core Framework Configuration (`com.framework.config`)

#### [NEW] [FrameworkConstants.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/config/FrameworkConstants.java)
Holds system paths (Reports, Properties, Payloads, Schemas) and execution parameters.

#### [NEW] [ConfigReader.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/config/ConfigReader.java)
A thread-safe Singleton property manager reading environment configurations (QA, UAT, PROD) dynamically based on `-Denv` (defaulting to `qa`).

---

### Centralized API Client Layer (`com.framework.api`)

#### [NEW] [BaseAPI.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/api/BaseAPI.java)
A wrapper class setting up thread-safe `RequestSpecification` and `ResponseSpecification`. Includes:
* Default configuration (headers, content-types).
* SSL/TLS bypass using `relaxedHTTPSValidation()`.
* Log-all rules (`log().all()`) and validation logs configuration (`log().ifValidationFails()`).

#### [NEW] [LogMaskFilter.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/api/LogMaskFilter.java)
Custom `io.restassured.filter.Filter` mapping request bodies, query params, and headers. Automatically filters and masks sensitive values (like `"password"` or `"token"`) with `[MASKED]` in execution logs.

#### [NEW] [RateLimitHandler.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/api/RateLimitHandler.java)
Interprets `429 Too Many Requests` responses, extracts the `Retry-After` header, and implements a backoff/retry mechanism for subsequent requests.

---

### Endpoints Wrapper Layer (`com.framework.endpoints`)

#### [NEW] [AuthAPI.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/endpoints/AuthAPI.java)
Decoupled request wrapper for authentication routines (`/api/login`, `/api/register`).

#### [NEW] [UserAPI.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/endpoints/UserAPI.java)
CRUD wrappers (`GET`, `POST`, `PUT`, `DELETE`) for user management, decoupling logic from assertions.

#### [NEW] [HttpBinAPI.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/endpoints/HttpBinAPI.java)
Wrappers for file operations, rate limiting tests (`/status/429`), and delay simulations.

---

### Data Models & POJOs (`com.framework.pojo`)

#### [NEW] [LoginPayload.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/pojo/LoginPayload.java)
Standard Lombok Model for Authentication.

#### [NEW] [UserPayload.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/pojo/UserPayload.java)
Model demonstrating Jackson serialization exclusions (`@JsonInclude(JsonInclude.Include.NON_NULL)`) for dynamic body building.

#### [NEW] [UserResponse.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/pojo/UserResponse.java)
Model for parsing API responses dynamically back into strongly-typed objects.

---

### Utilities & Custom TestNG Listeners (`com.framework.utils`)

#### [NEW] [DataGenerator.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/DataGenerator.java)
JavaFaker wrapper for dynamic test inputs.

#### [NEW] [JSONReader.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/JSONReader.java)
Utility parsing local JSON files to support data-driven testing suites.

#### [NEW] [RetryAnalyzer.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/RetryAnalyzer.java)
Implements `IRetryAnalyzer` to automatically re-execute failed test cases up to a configurable maximum.

#### [NEW] [AnnotationTransformer.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/AnnotationTransformer.java)
Implements `IAnnotationTransformer` to assign `RetryAnalyzer` dynamically across all test annotations.

#### [NEW] [ExtentReportManager.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/ExtentReportManager.java)
Thread-safe ExtentReports management with `ThreadLocal<ExtentTest>`.

#### [NEW] [FrameworkListener.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/FrameworkListener.java)
A comprehensive listener class implementing:
- `ISuiteListener`: Handles global setup and report instantiation/teardown.
- `ITestListener`: Reports on test start, success, failure, and skip. Includes attaching detailed request/response details.
- `IInvokedMethodListener`: Executes before and after every single test method invocation, printing active Thread IDs to illustrate parallel orchestration.

#### [NEW] [JSONCompareUtil.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/JSONCompareUtil.java)
Utility class wrapper around `JSONAssert` to compare actual response JSON with expected JSON files (supporting strict and lenient modes).

---

### Test Suites Layer (`src/test/java/com/framework/tests`)

#### [NEW] [BaseTest.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/BaseTest.java)
Standard setup and teardown class. Handles environment details loading.

#### [NEW] [AuthTest.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/AuthTest.java)
Covers diverse Auth configurations:
- Basic Authentication (`auth().basic()`).
- Preemptive Basic Authentication (`auth().preemptive().basic()`).
- Extracting Bearer Token from login payload and injecting it as `Authorization` header for subsequent requests.

#### [NEW] [UserCRUDTest.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/UserCRUDTest.java)
Standard CRUD tests with dependent methods (`dependsOnMethods`) to demonstrate sequential flow testing. Includes test case grouping (`groups = {"smoke", "regression"}`).

#### [NEW] [DataDrivenUserTest.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/DataDrivenUserTest.java)
Uses `@DataProvider(parallel = true)` to demonstrate concurrent data-driven tests.

#### [NEW] [ComplexJsonValidationTest.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/advanced/ComplexJsonValidationTest.java)
Focuses on deep JSON verification:
- Groovy GPath expressions in RestAssured's `JsonPath` (`findAll`, `find`, `collect`).
- Comparing actual response tree against expected JSON string using `JSONCompareUtil` (JSONAssert).
- Comprehensive Hamcrest Matcher assertions: `equalTo()`, `hasItem()`, `hasItems()`, `containsInAnyOrder()`, `hasSize()`, `notNullValue()`, and `containsString()`.
- Chaining validation assertions directly in RestAssured's fluent `.then()` interface vs extracting and validating with TestNG `SoftAssert` / AssertJ.

#### [NEW] [FileUploadDownloadTest.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/advanced/FileUploadDownloadTest.java)
Uploads multipart files using `multiPart()` and downloads binary streams.

#### [NEW] [RateLimitAndSlaTest.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/advanced/RateLimitAndSlaTest.java)
Tests SLA performance assertions and simulates Handling `429 Rate Limiting` with backoff logic.

---

### Resources Configuration (`src/test/resources`)

#### [NEW] [testng.xml](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/resources/testng.xml)
Configured to run classes in parallel using `parallel="classes" thread-count="3"`. Organizes tests using groups (`smoke` and `regression`).

#### [NEW] [env.qa.properties](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/resources/env.qa.properties)
QA environment configuration properties.

#### [NEW] [env.uat.properties](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/resources/env.uat.properties)
UAT environment configuration properties.

#### [NEW] [env.prod.properties](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/resources/env.prod.properties)
Prod environment configuration properties.

#### [NEW] [user-schema.json](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/resources/schemas/user-schema.json)
Schema mapping file for users.

#### [NEW] [expected-user.json](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/resources/expected-user.json)
Reference JSON to test expected vs actual body matches.

---

### CI/CD Integration

#### [NEW] [maven.yml](file:///e:/Learning/antigravity-projects/restassured-testng-app/.github/workflows/maven.yml)
GitHub Actions workflow file automating continuous integration:
- Sets up Java 17/21.
- Runs Maven tests pointing to the chosen environment (`mvn clean test -Denv=qa`).
- Archive and upload ExtentReports as build artifacts.

---

### Documentation

#### [NEW] [README.md](file:///e:/Learning/antigravity-projects/restassured-testng-app/README.md)
Contains structural diagrams, execution details, setup configs, and the **Top 15 API Automation Interview Q&A cheat-sheet** covering:
1. Handling thread-safety in parallel executions.
2. Logging and masking sensitive fields.
3. Custom Listeners implementation.
4. Groovy GPath expressions in JSONPath.
5. SoftAssert vs Hard Assert.
6. Rate limiting and SLA validations.
7. Dynamic payload serialization options.
8. Parallel DataProviders.
9. Actual vs Expected JSON trees verification.
10. Executing across QA, UAT, and PROD.
11. Managing authentication schemas.
12. Retry analyzer execution.
13. Chaining validations vs decoupling.
14. Handling file uploads and downloads.
15. CI/CD integration pipelines.

---

## Verification Plan

### Automated Tests
Run the entire suite locally:
```bash
mvn clean test
```
Verify environment parameters integration:
```bash
mvn clean test -Denv=uat
```

### Manual Verification
1. Inspect `target/reports/index.html` to confirm parallel execution formatting.
2. Review logs to confirm sensitive inputs (password, token) are masked correctly.
3. Verify that `FrameworkListener` logs thread IDs and suite-level parameters.
