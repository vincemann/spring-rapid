package com.github.vincemann.springrapid.coretest;

/**
 * Gets called by {@link InitializingTest} before each test class.
 */
public interface TestClassInitializable {
    default void beforeTestClass() throws Exception{}
    default void afterTestClass() throws Exception{}
}
