package com.framework.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class JSONReader {

    private static final Logger log = LoggerFactory.getLogger(JSONReader.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private JSONReader() {
        // Prevent instantiation
    }

    /**
     * Reads a JSON file containing a list of objects and parses it into a List of Maps.
     * Useful for setting up flexible data-driven tests.
     *
     * @param filePath Path to the JSON data file
     * @return List of Maps containing key-value test data
     */
    public static List<Map<String, String>> readTestData(String filePath) {
        log.info("Parsing test data file at path: {}", filePath);
        try {
            return mapper.readValue(new File(filePath), new TypeReference<List<Map<String, String>>>() {});
        } catch (IOException e) {
            String errorMsg = "Error reading JSON data from file: " + filePath;
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
}
