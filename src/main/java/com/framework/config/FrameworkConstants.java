package com.framework.config;

import java.io.File;

/**
 * Holds static final constants used globally across the automation framework.
 * This class cannot be instantiated.
 */
public final class FrameworkConstants {

    private FrameworkConstants() {
        // Prevent instantiation
    }

    /** Path to the root project directory. */
    public static final String PROJECT_PATH = System.getProperty("user.dir");

    /** Path to the test resources directory containing environment property files and mock JSON files. */
    public static final String RESOURCES_PATH = PROJECT_PATH + File.separator + "src" + File.separator + "test" + File.separator + "resources";

    /** Template path for locating properties files dynamically by environment (e.g. env.qa.properties). */
    public static final String ENV_PROPERTIES_PATH_TEMPLATE = RESOURCES_PATH + File.separator + "env.%s.properties";

    /** Output path for generating the HTML ExtentReports artifact. */
    public static final String REPORTS_PATH = PROJECT_PATH + File.separator + "target" + File.separator + "reports" + File.separator + "index.html";

    /** Path to the JSON schema validation directory. */
    public static final String SCHEMAS_PATH = RESOURCES_PATH + File.separator + "schemas";

    /** Path to the static JSON test data sheet. */
    public static final String TEST_DATA_PATH = RESOURCES_PATH + File.separator + "testdata.json";

    /** Default timeout value in milliseconds for network connection and socket read parameters. */
    public static final int DEFAULT_TIMEOUT = 5000;
}
