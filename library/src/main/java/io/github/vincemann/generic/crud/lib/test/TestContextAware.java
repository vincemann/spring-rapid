package io.github.vincemann.generic.crud.lib.test;

import java.util.List;

public interface TestContextAware<C extends InitializingTest> {
    public void setTestContext(C test);
}
