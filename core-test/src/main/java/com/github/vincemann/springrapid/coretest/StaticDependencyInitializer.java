package com.github.vincemann.springrapid.coretest;

public interface StaticDependencyInitializer extends TestMethodInitializable{

    @Override
    default void before() {
        initializeStaticDependencies();
    }

    void initializeStaticDependencies();
}
