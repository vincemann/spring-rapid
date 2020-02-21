package io.github.vincemann.generic.crud.lib.test;

public interface TestInitializable {
    public void init();
    public boolean supports(Class<? extends InitializingTest> testClass);
}
