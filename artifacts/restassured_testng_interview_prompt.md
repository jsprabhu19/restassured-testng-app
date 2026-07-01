# Comprehensive RestAssured & TestNG API Automation Framework Prompt

This document contains a production-grade, highly structured prompt engineered for advanced AI code generators (such as **antigravity** or custom LLM agents). It instructs the AI to build a complete, decoupled, and thread-safe API automation framework that serves as both a production reference and an ultimate interview readiness guide.

---

## The Master Prompt

```text
Act as a Principal QA Automation Architect and Elite SDET Instructor. I need you to generate a single, comprehensive, production-grade API Automation Framework using RestAssured and TestNG. 

This project must be designed for interview readiness and master-level reference. The codebase must be meticulously structured so that beginners can understand basic HTTP methods and validations, while seasoned experts can appreciate advanced architectural patterns (Hybrid Framework, Data-Driven, Thread-Safe Concurrency, and CI/CD readiness).

### Project Overview & Design Philosophy
1. Build a robust framework targeting a real-world mock API (e.g., an "E-Commerce Order Management API" or a "User Management & Auth System").
2. Do not just write boilerplate tests. Include extensive, crystal-clear inline documentation explaining the "Why" behind the architectural choices, common automation interview pitfalls, and optimization techniques.
3. Adhere strictly to the Page Object Model / Request Specification separation, ensuring zero hardcoding and complete decoupling of test data, configuration, and test logic.

### Framework Architectural Blueprint (Package by Package)

Please construct the project using the following structural layout:

1. `src/main/java/com/framework/config`
   - Framework Configuration Manager: Reading properties dynamically (Environment, BaseURI, Timeouts) using a Singleton pattern or Owner library style.
   - Global Constants file.

2. `src/main/java/com/framework/api`
   - BaseAPI Client: Centralized setup for `RequestSpecification` and `ResponseSpecification` using `RequestSpecBuilder`.
   - Implementation of global logging (Request/Response logging filters) and custom error handling interceptors.

3. `src/main/java/com/framework/endpoints`
   - Endpoints Wrapper: Separate classes for different sub-domains (e.g., `AuthAPI.java`, `UserAPI.java`, `ProductAPI.java`).
   - Clean, fluent-style methods returning RestAssured `Response` objects, separating the API call mechanics from the TestNG validation layer.

4. `src/main/java/com/framework/pojo`
   - Advanced Serialization/Deserialization models.
   - Demonstration of Lombok (`@Data`, `@Builder`, `@Jacksonized`) to construct dynamic payloads.
   - Nested JSON payloads representation using Java objects.

5. `src/main/java/com/framework/utils`
   - `DataGenerator`: Using JavaFaker for dynamic test data generation.
   - `ExcelReader` / `JSONReader`: Utilities to support Data-Driven Testing.
   - Extent Reports / Allure Reports listener utility for rich, graphical test reporting.

6. `src/test/java/com/framework/tests`
   - CRUD Operations: A complete sequence of `POST` -> `GET` -> `PUT` -> `DELETE` tests demonstrating state dependency management.
   - Authentication Handling: Demonstrating Bearer Tokens, OAuth2, and Cookie-based authentication, explicitly showing how to extract a token from a login response and inject it into subsequent requests.

7. `src/test/java/com/framework/tests/advanced`
   - Complex Assertions: JSON Schema Validation, deep JSON path extractions, body validations using Hamcrest Matchers vs AssertJ.
   - Multi-part Form Data upload and File Download validation tests.
   - Time/Performance Assertions: Validating response SLA/response times.

8. `src/test/resources`
   - `testng.xml`: Configured for parallel test execution (`parallel="tests"`, `thread-count`).
   - Test data suites (`testdata.xlsx` or `payloads.json`) and `schema.json` for validation.
   - Environment property files (`env.dev.properties`, `env.staging.properties`).

### Core Concepts & Interview Scenarios to Demonstrate
The framework code must explicitly address and show solutions for these highly popular interview questions via concrete implementations:
- How do you handle thread-safety when running RestAssured tests in parallel via TestNG? (Demonstrate `ThreadLocal` for Request/Response or Thread-Safe design).
- How do you serialize dynamically changing payloads without creating dozens of hardcoded POJO classes? (Demonstrate Jackson features like `@JsonInclude(JsonInclude.Include.NON_NULL)` or dynamic Maps).
- How do you implement a custom RestAssured Filter to mask sensitive data (like passwords/tokens) in your execution logs?

### Output Deliverables
- **`pom.xml`**: A fully configured Maven file containing compatible dependencies for RestAssured, TestNG, Jackson/Gson, Lombok, ExtentReports/Allure, and Maven Surefire Plugin configured for TestNG suites.
- **Flawlessly Executable Code**: Ensure all mock endpoints are documented so they could run seamlessly against a tool like ReqRes, JSONPlaceholder, or local Mockoon.
- **Accompanying `README.md`**: Highlighting the architecture, command-line execution guides (`mvn clean test -Denv=staging`), and a cheat-sheet answering the top 15 RestAssured/TestNG interview questions using examples directly from this codebase.

Provide the complete directory structure and code for these classes cleanly.
```

---

## Why this Architecture Wins SDET Interviews

1. **Decoupled Engine Design**: Separating the Request Construction (`src/main/java`) from the Test validation layer (`src/test/java`) shows mature understanding of reusable framework patterns.
2. **Dynamic Over Static Data**: Using Lombok builders and dynamic test generation (JavaFaker) avoids stale test datasets, a major talking point in modern pipeline execution.
3. **Thread Safety**: Using explicit parallel configurations in `testng.xml` coupled with non-interfering request variables validates your ability to scale automation across enterprise microservices.
