package io.github.vincemann.generic.crud.lib.test;

public interface TestContextAware<C extends InitializingTest> {
    public void setTestContext(C test);
    public boolean supports(Class<? extends InitializingTest> contextClass);
}
