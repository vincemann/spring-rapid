package io.github.vincemann.generic.crud.lib.test;

public interface TestAware<T extends InitializingTest> {
    public void setTest(T test);
    public  T getTest();
}
