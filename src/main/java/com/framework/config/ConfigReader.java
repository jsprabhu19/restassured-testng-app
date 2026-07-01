package com.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private final Properties properties = new Properties();

    // Bill Pugh Singleton Pattern - Thread-safe and Lazy
    private static class Holder {
        private static final ConfigReader INSTANCE = new ConfigReader();
    }

    public static ConfigReader getInstance() {
        return Holder.INSTANCE;
    }

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

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log.warn("Property key '{}' not found in properties.", key);
            return null;
        }
        return value.trim();
    }

    public String getString(String key) {
        return get(key);
    }

    public int getInt(String key) {
        String value = get(key);
        if (value == null) {
            return FrameworkConstants.DEFAULT_TIMEOUT;
        }
        return Integer.parseInt(value);
    }

    public String getBaseUriReqRes() {
        return get("base.uri.reqres");
    }

    public String getBaseUriHttpBin() {
        return get("base.uri.httpbin");
    }
}
