package com.github.vincemann.springrapid.coretest.bootstrap;


import com.github.vincemann.springrapid.core.bootstrap.DatabaseDataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.List;

/**
 * Calls {@link DatabaseDataInitializer} that need to run before each test.
 */
public class InitializerTestExecutionListener extends AbstractTestExecutionListener {

    @Autowired
    @BeforeEachTestInitializable
    private List<DatabaseDataInitializer> beforeEachTest;

    @Autowired
    @BeforeEachTestMethodInitializable
    private List<DatabaseDataInitializer> beforeEachTestMethod;

    @Override
    public void beforeTestClass(TestContext testContext) {
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        for (DatabaseDataInitializer initializer : beforeEachTest) {
            initializer.init();
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        for (DatabaseDataInitializer initializer : beforeEachTestMethod) {
            initializer.init();
        }
    }
}
