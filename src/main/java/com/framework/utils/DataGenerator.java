package com.framework.utils;

import com.github.javafaker.Faker;

public final class DataGenerator {

    private static final Faker faker = new Faker();

    private DataGenerator() {
        // Prevent instantiation
    }

    public static String getFullName() {
        return faker.name().fullName();
    }

    public static String getJobTitle() {
        return faker.job().title();
    }

    public static String getEmail() {
        return faker.internet().emailAddress();
    }

    public static String getPassword() {
        return faker.internet().password(6, 12, true, true, true);
    }
}
