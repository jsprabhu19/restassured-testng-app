# Decoupled, Thread-Safe API Automation Framework (RestAssured + TestNG)

This repository contains a production-grade, highly structured API Automation Framework using RestAssured and TestNG. It is engineered to serve as both an enterprise-level automation baseline and a comprehensive, master-level interview readiness reference.

---

## 🏗️ Project Architecture Layout

The project follows a decoupled architecture, cleanly separating request configuration, model definitions, endpoints logic, custom filters, listeners, and validation suites:

```text
restassured-testng-app/
├── .github/workflows/
│   └── maven.yml                 # CI/CD integration workflow
├── src/
│   ├── main/java/com/framework/
│   │   ├── api/
│   │   │   ├── BaseAPI.java             # Spec factory with SSL bypass and filters
│   │   │   ├── LogMaskFilter.java       # Logging filter with PII data masking
│   │   │   └── RateLimitHandler.java    # Auto-retry handler for HTTP 429 errors
│   │   ├── config/
│   │   │   ├── ConfigReader.java        # Thread-safe Singleton environment config reader
│   │   │   └── FrameworkConstants.java  # Global paths and defaults
│   │   ├── endpoints/
│   │   │   ├── AuthAPI.java             # Authentication request wrappers
│   │   │   ├── HttpBinAPI.java          # File upload/download and status wrappers
│   │   │   └── UserAPI.java             # User CRUD request wrappers
│   │   ├── pojo/
│   │   │   ├── LoginPayload.java        # Serialization POJO for Auth
│   │   │   ├── LoginResponse.java       # Deserialization POJO for Auth response
│   │   │   ├── UserPayload.java         # Serialization POJO using NON_NULL exclusions
│   │   │   └── UserResponse.java        # Nested Deserialization POJO mapping data objects
│   │   └── utils/
│   │       ├── AnnotationTransformer.java # Dynamic test annotation modifiers
│   │       ├── DataGenerator.java       # JavaFaker data generation helper
│   │       ├── ExtentReportManager.java # Thread-safe report writer (ThreadLocal)
│   │       ├── FrameworkListener.java   # Suite, Test, and Method listener logs
│   │       ├── JSONCompareUtil.java     # JSONAssert matcher wrapper
│   │       ├── JSONReader.java          # External JSON data source parser
│   │       └── RetryAnalyzer.java       # Automated failed test execution retry analyzer
│   └── test/
│       ├── java/com/framework/tests/
│       │   ├── advanced/
│       │   │   ├── ComplexJsonValidationTest.java # Schema, GPath, and SoftAssert tests
│       │   │   ├── FileUploadDownloadTest.java    # Multipart uploads & binary downloads
│       │   │   └── RateLimitAndSlaTest.java       # SLA validations & 429 retry loops
│       │   ├── AuthTest.java            # Basic, Preemptive, and Bearer token auth tests
│       │   ├── BaseTest.java            # Suite-level initialization hooks
│       │   ├── DataDrivenUserTest.java  # Concurrent JSON-based data-driven tests
│       │   └── UserCRUDTest.java        # Sequential dependent test suite (POST/GET/PUT/DELETE)
│       └── resources/
│           ├── env.qa.properties        # QA configurations
│           ├── env.uat.properties       # UAT configurations
│           ├── env.prod.properties      # Prod configurations
│           ├── schemas/
│           │   └── user-schema.json     # User response validation schema
│           ├── expected-user.json       # Reference JSON for actual vs expected testing
│           ├── testdata.json            # Parameters for data-driven tests
│           └── testng.xml               # Suite file config with parallel settings
├── artifacts/
└── pom.xml                              # Build and dependency configuration
```

---

## 🚀 Execution & Command-Line Guide

### Run Tests in the default environment (QA)
```bash
mvn clean test
```

### Run Tests under a specific environment (UAT or PROD)
The framework loads the corresponding property file dynamically using the system property `-Denv`:
```bash
# Executing on UAT
mvn clean test -Denv=uat

# Executing on PROD
mvn clean test -Denv=prod
```

### Run a specific Group of tests
```bash
mvn clean test -Dgroups=smoke
```

---

## 📊 Rich HTML Test Reporting

On execution completion, a thread-safe Extent Report is generated at:
`target/reports/index.html`

The report automatically captures request headers, masked payloads, response codes, response times, and failure stacktraces without any cross-thread logging pollution.

---

## 💡 Top 15 API Automation Interview Q&A (Framework Cheat-Sheet)

Here is a guide explaining how this codebase implements solutions to the most common advanced API automation interview questions.

### 1. How do you handle thread-safety when running RestAssured tests in parallel?
* **Problem**: Sharing a static `RequestSpecification` or `ExtentTest` instance across concurrent threads causes race conditions, leading to scrambled parameters and corrupted reports.
* **Code Solution**: 
  - In [BaseAPI.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/api/BaseAPI.java), we instantiate a fresh `RequestSpecBuilder` and return a new `RequestSpecification` local to the executing method context.
  - In [ExtentReportManager.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/ExtentReportManager.java), we declare `private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();`. This keeps reports strictly isolated per thread.

### 2. How do you serialize dynamically changing payloads without creating dozens of hardcoded POJOs?
* **Problem**: An endpoint might require a payload with optional fields. Creating a class for every combination violates clean code.
* **Code Solution**:
  - In [UserPayload.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/pojo/UserPayload.java), we apply `@JsonInclude(JsonInclude.Include.NON_NULL)`. Any field set to `null` is automatically omitted from the serialized JSON payload.
  - Alternatively, you can pass a `Map<String, Object>` built dynamically using a fluent map structure.

### 3. How do you implement a custom RestAssured Filter to mask sensitive data?
* **Problem**: Printing passwords, credit card numbers, or authorization tokens into logs violates security compliance.
* **Code Solution**:
  - In [LogMaskFilter.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/api/LogMaskFilter.java), we implement `io.restassured.filter.Filter`. We intercept the raw request/response payload strings and use regex mapping to mask keys like `"password"` or `"token"`, replacing values with `[MASKED]` before logging.

### 4. How do you test or automatically handle Rate Limiting (HTTP 429)?
* **Problem**: When a server receives too many queries, it returns status 429 and a `Retry-After` header.
* **Code Solution**:
  - In [RateLimitHandler.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/api/RateLimitHandler.java), we check if `response.getStatusCode() == 429`. If so, we parse the `Retry-After` header value, invoke a `Thread.sleep()` backoff, and automatically re-submit the request up to a maximum limit.

### 5. What is the difference between pathParam(), queryParam(), and formParam()?
* **Code Reference**: Demonstrations reside in:
  - `pathParam()`: Appends variables inside the URI template, e.g., `/api/users/{id}` in [UserAPI.java:31](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/endpoints/UserAPI.java#L31).
  - `queryParam()`: Appends key-values after a question mark, e.g., `/api/users?page=2` in [UserAPI.java:42](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/endpoints/UserAPI.java#L42).
  - `formParam()`: Passes parameters within the request body (typically content-type `application/x-www-form-urlencoded`).

### 6. How do you validate expected vs actual JSON object data instead of single fields?
* **Problem**: Validating a JSON response key-by-key is tedious. We need to assert structural matching.
* **Code Solution**:
  - In [JSONCompareUtil.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/JSONCompareUtil.java), we wrap the **JSONAssert** library. We load a complete expected JSON template and compare it against the actual response string, supporting `LENIENT` mode to bypass extra keys/order.

### 7. How do you perform deep queries on response JSON payloads? (Groovy GPath)
* **Problem**: Extracting nested data values or filtering elements matching conditions requires verbose Java loops.
* **Code Solution**:
  - In [ComplexJsonValidationTest.java:23](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/advanced/ComplexJsonValidationTest.java#L23), we use RestAssured's JSONPath which natively runs Groovy GPath:
    - Filter collection: `data.findAll { it.id > 9 }`
    - Find matching node: `data.find { it.first_name == 'George' }`
    - Collect properties: `data.collect { it.email }`
    - Calculate aggregate value: `data.max { it.id }`

### 8. What are SoftAssert vs Hard Assertions in API automation?
* **Code Reference**: Demonstrations reside in [ComplexJsonValidationTest.java:78](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/advanced/ComplexJsonValidationTest.java#L78):
  - **Hard Assertion**: Stops the test execution immediately upon the first failure.
  - **Soft Assert**: TestNG's `SoftAssert` logs failures for multiple validations and continues execution. All logged errors are reported at the end when `soft.assertAll()` is invoked.

### 9. How do you handle SSL/TLS certificate errors in RestAssured?
* **Problem**: Testing environments often use untrusted self-signed SSL certificates, throwing SSLHandshakeException.
* **Code Solution**:
  - In [BaseAPI.java:31](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/api/BaseAPI.java#L31), we attach `.setRelaxedHTTPSValidation()` to the `RequestSpecBuilder`. This bypasses cert validation checks globally.

### 10. How do you implement retry analyzer logic for flaky API tests?
* **Problem**: Network instability can cause API tests to fail. We need automated retries before marking a test as failed.
* **Code Solution**:
  - We implement `IRetryAnalyzer` in [RetryAnalyzer.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/RetryAnalyzer.java).
  - To hook it dynamically without annotating every test method, we implement `IAnnotationTransformer` in [AnnotationTransformer.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/AnnotationTransformer.java) and register it in `testng.xml`.

### 11. When do you use RestAssured fluent chaining (.then()) vs decoupled extraction?
* **Code Reference**: Demonstrations reside in [ComplexJsonValidationTest.java:66](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/advanced/ComplexJsonValidationTest.java#L66):
  - **Chaining**: Inline validations using `.then().body(...)` are clean for simple tests but couple assertion failures directly to the HTTP execution.
  - **Decoupled**: Extracting the `Response` object allows using AssertJ or TestNG SoftAssert, which is the preferred production standard as it decouples the request mechanics from assertion strategies.

### 12. Explain the purpose and role of TestNG ISuiteListener, ITestListener, and IInvokedMethodListener.
* **Code Reference**: Implemented in [FrameworkListener.java](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/utils/FrameworkListener.java):
  - `ISuiteListener`: Runs before/after the entire suite (ideal for global setups like starting/saving Extent reports).
  - `ITestListener`: Listens to test method lifecycles (logs test status - Pass, Fail, Skip, and handles screenshots or reports mapping).
  - `IInvokedMethodListener`: Executes before/after every single configuration or test method (ideal for logging Thread IDs during parallel execution).

### 13. How do you implement a parallel DataProvider?
* **Problem**: Serial DataDriven testing executes slowly. We need parallel execution.
* **Code Solution**:
  - In [DataDrivenUserTest.java:18](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/test/java/com/framework/tests/DataDrivenUserTest.java#L18), we declare `@DataProvider(name = "userDataProvider", parallel = true)`. TestNG triggers multiple worker threads to execute user creation requests concurrently.

### 14. How do you handle file uploads and binary downloads in RestAssured?
* **Code Reference**: Implemented in [HttpBinAPI.java:23](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/endpoints/HttpBinAPI.java#L23):
  - **Upload**: Set content-type to `multipart/form-data` and use `.multiPart("file", file)` to attach the payload.
  - **Download**: Extract the response as a raw byte array `.asByteArray()`, which can be validated or saved as a local binary file.

### 15. How do you configure multi-environment runs and CI/CD integration?
* **Code Solution**:
  - Property files for each environment (`env.qa.properties`, `env.uat.properties`, `env.prod.properties`) are placed in `src/test/resources/`.
  - In [ConfigReader.java:23](file:///e:/Learning/antigravity-projects/restassured-testng-app/src/main/java/com/framework/config/ConfigReader.java#L23), we load files dynamically based on system property `-Denv`.
  - CI/CD workflow is defined in [maven.yml](file:///e:/Learning/antigravity-projects/restassured-testng-app/.github/workflows/maven.yml), triggering `mvn clean test -Denv=qa` on pushes, then archiving reports.
