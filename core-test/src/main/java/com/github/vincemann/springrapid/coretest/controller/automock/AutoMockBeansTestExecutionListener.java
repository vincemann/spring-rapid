package com.github.vincemann.springrapid.coretest.controller.automock;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class AutoMockBeansTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        AutoMockBeanFactory autoMockBeanFactory = (AutoMockBeanFactory) testContext.getApplicationContext()
                .getAutowireCapableBeanFactory();
        autoMockBeanFactory.resetMocks();
        super.afterTestMethod(testContext);
    }
}
