package com.github.vincemann.springrapid.coretest;

/**
 * Gets called by {@link InitializingTest} before each test class.
 */
public interface TestInitializable {
    public default void before(){}
    public default void after(){}
}
