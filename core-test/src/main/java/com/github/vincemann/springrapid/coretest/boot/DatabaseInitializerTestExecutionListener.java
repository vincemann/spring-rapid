package com.github.vincemann.springrapid.coretest.boot;


import com.github.vincemann.springrapid.core.boot.DatabaseInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.List;

/**
 * Calls {@link DatabaseInitializer} that need to run before each test.
 */
public class DatabaseInitializerTestExecutionListener extends AbstractTestExecutionListener {

    @Autowired(required = false)
    @BeforeEachTestInitializable
    private List<DatabaseInitializer> beforeEachTest;

    @Autowired(required = false)
    @BeforeEachTestMethodInitializable
    private List<DatabaseInitializer> beforeEachTestMethod;

    @Override
    public void beforeTestClass(TestContext testContext) {
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        if (beforeEachTest != null) {
            for (DatabaseInitializer initializer : beforeEachTest) {
                initializer.init();
            }
        }

    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        if (beforeEachTestMethod != null) {
            for (DatabaseInitializer initializer : beforeEachTestMethod) {
                initializer.init();
            }
        }
    }
}
