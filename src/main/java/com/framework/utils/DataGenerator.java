package com.framework.utils;

import com.github.javafaker.Faker;

/**
 * Utility class utilizing Java Faker to generate randomized mock payloads.
 * Prevents hardcoding values in test suites and ensures dynamic data inputs.
 */
public final class DataGenerator {

    private static final Faker faker = new Faker();

    private DataGenerator() {
        // Prevent instantiation
    }

    /**
     * Generates a random full name.
     *
     * @return Generated name string
     */
    public static String getFullName() {
        return faker.name().fullName();
    }

    /**
     * Generates a random corporate job title.
     *
     * @return Generated job title string
     */
    public static String getJobTitle() {
        return faker.job().title();
    }

    /**
     * Generates a random, syntactically valid email address.
     *
     * @return Generated email address string
     */
    public static String getEmail() {
        return faker.internet().emailAddress();
    }

    /**
     * Generates a random password conforming to length criteria.
     *
     * @return Generated password string of length 6 to 12
     */
    public static String getPassword() {
        return faker.internet().password(6, 12, true, true, true);
    }
}
