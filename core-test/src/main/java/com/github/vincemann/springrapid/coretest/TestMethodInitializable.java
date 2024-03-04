package com.github.vincemann.springrapid.coretest;

public interface TestMethodInitializable{
    public default void beforeTestMethod() throws Exception {}
    public default void afterTestMethod() throws Exception{}
}
