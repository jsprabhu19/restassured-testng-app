package com.framework.utils;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class JSONCompareUtil {

    private static final Logger log = LoggerFactory.getLogger(JSONCompareUtil.class);

    private JSONCompareUtil() {
        // Prevent instantiation
    }

    /**
     * Asserts that actual JSON string matches the contents of an expected JSON string.
     *
     * @param expectedJson Expected JSON string
     * @param actualJson   Actual JSON string received from API
     * @param strictMode   If true, checks for exact order and structure equality. If false, ignores extra fields and array ordering.
     */
    public static void assertJsonEquality(String expectedJson, String actualJson, boolean strictMode) {
        JSONCompareMode mode = strictMode ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT;
        log.info("Comparing actual response JSON to expected JSON. Mode: {}", mode);
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, mode);
            log.info("JSON comparison matched successfully!");
        } catch (AssertionError e) {
            log.error("JSON structure comparison mismatch detected!", e);
            throw e;
        } catch (org.json.JSONException e) {
            log.error("Failed to parse JSON strings for assertion!", e);
            throw new RuntimeException("JSON processing error during assertion", e);
        }
    }

    /**
     * Asserts that actual JSON string matches the contents of a stored expected JSON template file.
     *
     * @param expectedFilePath Absolute path to the expected JSON file
     * @param actualJson       Actual JSON string received from API
     * @param strictMode       Strict or lenient matching mode
     */
    public static void assertJsonFileEquality(String expectedFilePath, String actualJson, boolean strictMode) {
        log.info("Reading expected JSON reference file from path: {}", expectedFilePath);
        try {
            String expectedJson = new String(Files.readAllBytes(Paths.get(expectedFilePath)));
            assertJsonEquality(expectedJson, actualJson, strictMode);
        } catch (IOException e) {
            String errorMsg = "Failed to load expected JSON file from path: " + expectedFilePath;
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
}
