package com.github.vincemann.springrapid.coretest;

public interface TestContextAware<C extends InitializingTest> {
    public void setTestContext(C test);
}
