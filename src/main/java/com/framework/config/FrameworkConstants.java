package com.framework.config;

import java.io.File;

public final class FrameworkConstants {

    private FrameworkConstants() {
        // Prevent instantiation
    }

    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String RESOURCES_PATH = PROJECT_PATH + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    public static final String ENV_PROPERTIES_PATH_TEMPLATE = RESOURCES_PATH + File.separator + "env.%s.properties";
    public static final String REPORTS_PATH = PROJECT_PATH + File.separator + "target" + File.separator + "reports" + File.separator + "index.html";
    public static final String SCHEMAS_PATH = RESOURCES_PATH + File.separator + "schemas";
    public static final String TEST_DATA_PATH = RESOURCES_PATH + File.separator + "testdata.json";

    public static final int DEFAULT_TIMEOUT = 5000;
}
