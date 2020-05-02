package io.github.vincemann.springrapid.coretest.bootstrap;


import io.github.vincemann.springrapid.core.bootstrap.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.List;

/**
 * Calls {@link Initializer} that need to run before each test.
 */
public class InitializerTestExecutionListener extends AbstractTestExecutionListener {

    @Autowired
    @BeforeEachTestInitializable
    private List<Initializer> beforeEachTest;

    @Autowired
    @BeforeEachTestMethodInitializable
    private List<Initializer> beforeEachTestMethod;

    @Override
    public void beforeTestClass(TestContext testContext) {
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        for (Initializer initializer : beforeEachTest) {
            initializer.init();
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        for (Initializer initializer : beforeEachTestMethod) {
            initializer.init();
        }
    }
}
