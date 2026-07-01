package com.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader loads environment configuration files dynamically at runtime
 * and parses parameters (URIs, timeouts). System properties take precedence over properties files.
 * Implementation utilizes the Bill Pugh Singleton pattern for thread-safety and lazy initialization.
 */
public final class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private final Properties properties = new Properties();

    /** Bill Pugh Singleton class holder. */
    private static class Holder {
        private static final ConfigReader INSTANCE = new ConfigReader();
    }

    /**
     * Retrieves the single thread-safe instance of ConfigReader.
     *
     * @return ConfigReader instance
     */
    public static ConfigReader getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Private constructor that resolves the target environment key, determines the template path,
     * and streams/loads properties file parameters into memory.
     */
    private ConfigReader() {
        String env = System.getProperty("env");
        if (env == null || env.trim().isEmpty()) {
            env = "qa";
            log.info("System property 'env' was not set. Defaulting to 'qa'.");
        } else {
            env = env.trim().toLowerCase();
            log.info("System property 'env' is set to '{}'.", env);
        }

        String propertiesFilePath = String.format(FrameworkConstants.ENV_PROPERTIES_PATH_TEMPLATE, env);
        log.info("Loading environment configuration file: {}", propertiesFilePath);

        try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
            properties.load(fis);
            log.info("Successfully loaded environment: {}", properties.getProperty("environment"));
        } catch (IOException e) {
            String errorMsg = "Failed to load environment property file for environment: " + env + " at path: " + propertiesFilePath;
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Resolves property values. System property overrides are given priority.
     *
     * @param key Config variable key name
     * @return Resolved property value, or null if key does not exist
     */
    public String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.trim().isEmpty()) {
            log.info("System property override found for key '{}': {}", key, systemValue);
            return systemValue.trim();
        }

        String value = properties.getProperty(key);
        if (value == null) {
            log.warn("Property key '{}' not found in properties.", key);
            return null;
        }
        return value.trim();
    }

    /**
     * Retrieves target config property as a String type.
     *
     * @param key Variable key name
     * @return Resolved string property value
     */
    public String getString(String key) {
        return get(key);
    }

    /**
     * Resolves configuration key as an integer value. Returns default timeout fallback if key missing.
     *
     * @param key Variable key name
     * @return Resolved integer configuration value
     */
    public int getInt(String key) {
        String value = get(key);
        if (value == null) {
            return FrameworkConstants.DEFAULT_TIMEOUT;
        }
        return Integer.parseInt(value);
    }

    /**
     * Helper mapping method to get the base ReqRes REST service endpoint.
     *
     * @return ReqRes base URI string
     */
    public String getBaseUriReqRes() {
        return get("base.uri.reqres");
    }

    /**
     * Helper mapping method to get the base HttpBin mocking server endpoint.
     *
     * @return HttpBin base URI string
     */
    public String getBaseUriHttpBin() {
        return get("base.uri.httpbin");
    }
}
