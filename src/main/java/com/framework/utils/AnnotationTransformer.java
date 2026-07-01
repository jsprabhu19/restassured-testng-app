package com.framework.utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG Annotation Transformer listener.
 * Dynamically binds our custom {@link RetryAnalyzer} to all test annotations at runtime,
 * eliminating the need to explicitly specify the retryAnalyzer attribute on individual test methods.
 */
public class AnnotationTransformer implements IAnnotationTransformer {

    /**
     * Intercepts TestNG test annotations at runtime to dynamically inject the RetryAnalyzer class.
     *
     * @param annotation The target test annotation being modified
     * @param testClass The test class containing the annotated method
     * @param testConstructor The constructor of the test class (if applicable)
     * @param testMethod The test method being loaded
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Dynamically attach the RetryAnalyzer to all tests throughout execution
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
