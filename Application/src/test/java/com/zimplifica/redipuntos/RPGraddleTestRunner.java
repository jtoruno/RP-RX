package com.zimplifica.redipuntos;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;


public class RPGraddleTestRunner extends RobolectricTestRunner {
    public static final int DEFAULT_SDK = 21;

    public RPGraddleTestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);
    }
}