package com.github.vincemann.springrapid.coretest;

public interface TestMethodInitializable{
    public default void beforeTestMethod(){}
    public default void afterTestMethod(){}
}
