package io.github.vincemann.springrapid.coretest.bootstrap;


import io.github.vincemann.springrapid.core.bootstrap.DatabaseDataInitializer;
import io.github.vincemann.springrapid.core.bootstrap.DatabaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.List;

/**
 * Calls {@link DatabaseDataInitializer} that need to run before each test.
 */
public class InitTestDataTestExecutionListener extends AbstractTestExecutionListener {

    @Autowired
    @BeforeEachTestInitializable
    private List<DatabaseDataInitializer> databaseDataInitializers;

    @Override
    public void beforeTestClass(TestContext testContext) {
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        for (DatabaseDataInitializer databaseDataInitializer : databaseDataInitializers) {
            databaseDataInitializer.loadInitData();
        }
    }






}
