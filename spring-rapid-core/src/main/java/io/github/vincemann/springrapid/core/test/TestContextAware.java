package io.github.vincemann.springrapid.core.test;

public interface TestContextAware<C extends InitializingTest> {
    public void setTestContext(C test);
}
