package com.framework.api;

import com.framework.utils.ExtentReportManager;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom REST Assured Filter that intercepts API requests and responses, formats them,
 * masks sensitive configuration data (such as passwords, secrets, or tokens), and logs
 * the details to both SLF4J logger and the ExtentReport manager.
 */
public class LogMaskFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LogMaskFilter.class);

    /**
     * Intercepts HTTP requests and responses, applies masking, and logs request/response
     * details before delegating the execution downstream.
     *
     * @param requestSpec Request specification being logged
     * @param responseSpec Response specification being logged
     * @param ctx RestAssured filter context
     * @return Raw HTTP Response
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        // Intercept and format Request log
        String maskedRequest = formatRequestLog(requestSpec);
        log.info(maskedRequest);
        ExtentReportManager.logInfo("<pre>" + maskedRequest + "</pre>");

        // Execute actual request
        Response response = ctx.next(requestSpec, responseSpec);

        // Intercept and format Response log
        String maskedResponse = formatResponseLog(response);
        log.info(maskedResponse);
        ExtentReportManager.logInfo("<pre>" + maskedResponse + "</pre>");

        return response;
    }

    /**
     * Formats API Request components including Method, URI, Headers (with Auth masking),
     * and Request Body (with field-level masking).
     *
     * @param requestSpec Request details to format
     * @return Formatted request log string
     */
    private String formatRequestLog(FilterableRequestSpecification requestSpec) {
        StringBuilder reqLog = new StringBuilder();
        reqLog.append("\n=================== API REQUEST ===================\n");
        reqLog.append("Method: ").append(requestSpec.getMethod()).append("\n");
        reqLog.append("URI   : ").append(requestSpec.getURI()).append("\n");
        
        reqLog.append("Headers:\n");
        if (requestSpec.getHeaders() != null) {
            requestSpec.getHeaders().forEach(h -> {
                if (h.getName().equalsIgnoreCase("Authorization") || h.getName().equalsIgnoreCase("Token") || h.getName().equalsIgnoreCase("x-auth-token")) {
                    reqLog.append("  ").append(h.getName()).append(": [MASKED]\n");
                } else {
                    reqLog.append("  ").append(h.getName()).append(": ").append(h.getValue()).append("\n");
                }
            });
        }

        Object body = requestSpec.getBody();
        if (body != null) {
            reqLog.append("Body  : ").append(maskSensitiveFields(body.toString())).append("\n");
        } else {
            reqLog.append("Body  : EMPTY\n");
        }
        reqLog.append("===================================================\n");
        return reqLog.toString();
    }

    /**
     * Formats API Response components including Status Code, Response Time, Headers,
     * and Response Body with sensitive information masked.
     *
     * @param response Response object to format
     * @return Formatted response log string
     */
    private String formatResponseLog(Response response) {
        StringBuilder resLog = new StringBuilder();
        resLog.append("\n=================== API RESPONSE ==================\n");
        resLog.append("Status Code: ").append(response.getStatusCode()).append(" (").append(response.getStatusLine().trim()).append(")\n");
        resLog.append("Response Time: ").append(response.getTime()).append(" ms\n");
        
        resLog.append("Headers:\n");
        if (response.getHeaders() != null) {
            response.getHeaders().forEach(h -> {
                if (h.getName().equalsIgnoreCase("Set-Cookie") || h.getName().equalsIgnoreCase("Authorization")) {
                    resLog.append("  ").append(h.getName()).append(": [MASKED]\n");
                } else {
                    resLog.append("  ").append(h.getName()).append(": ").append(h.getValue()).append("\n");
                }
            });
        }

        String body = response.getBody().asString();
        if (body != null && !body.trim().isEmpty()) {
            resLog.append("Body       : ").append(maskSensitiveFields(body)).append("\n");
        } else {
            resLog.append("Body       : EMPTY\n");
        }
        resLog.append("===================================================\n");
        return resLog.toString();
    }

    /**
     * Scans JSON content or text inputs to mask sensitive security keys.
     * Target keywords include password, token, secret, token_id, pwd, passwd.
     *
     * @param input Raw text payload
     * @return Payload with sensitive details masked
     */
    private String maskSensitiveFields(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        // Mask specific JSON keys (e.g. "password":"xyz", "token":"abc", "token_id":"123")
        // Matching both string and numeric/boolean formats
        return input
                .replaceAll("(?i)\"((?:password|token|secret|token_id|pwd|passwd))\"\\s*:\\s*\"([^\"]+)\"", "\"$1\":\"[MASKED]\"")
                .replaceAll("(?i)\"((?:password|token|secret|token_id|pwd|passwd))\"\\s*:\\s*([0-9a-zA-Z]+)", "\"$1\":[MASKED]");
    }
}
