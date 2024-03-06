package com.github.vincemann.springrapid.coretest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes all members of test that impl {@link TestMethodInitializable}s and/or
 * {@link TestClassInitializable}.
 *
 * Reflection depth = 1
 */
public class InitializingTestExecutionListener extends AbstractTestExecutionListener {

    @Autowired(required = false)
    private List<TestMethodInitializable> methodInitializables = new ArrayList<>();
    private boolean beforeEachInitialized = false;
    private boolean afterEachInitialized = false;


    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        super.beforeTestMethod(testContext);
        autowireDeps(testContext);

        if (!beforeEachInitialized) {
            ReflectionUtils.doWithFields(testContext.getTestClass(), field -> {
                try {
                    ReflectionUtils.makeAccessible(field);
                    Object member = field.get(testContext.getTestInstance());
                    if (member instanceof TestClassInitializable) {
                        ((TestClassInitializable) member).beforeTestClass();
                    }
                    if (member instanceof TestMethodInitializable) {
                        methodInitializables.add((TestMethodInitializable) member);
                        ((TestMethodInitializable) member).beforeTestMethod();
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }

            });
        } else {
            for (TestMethodInitializable methodInitializable : methodInitializables) {
                methodInitializable.beforeTestMethod();
            }
        }
        beforeEachInitialized = true;
    }

    private void autowireDeps(TestContext testContext){
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        super.afterTestMethod(testContext);
        if (!afterEachInitialized) {
            ReflectionUtils.doWithFields(testContext.getTestClass(), field -> {
                try {
                    ReflectionUtils.makeAccessible(field);
                    Object member = field.get(testContext.getTestInstance());
                    if (member instanceof TestClassInitializable) {
                        ((TestClassInitializable) member).afterTestClass();
                    }
                    if (member instanceof TestMethodInitializable) {
                        methodInitializables.add((TestMethodInitializable) member);
                        ((TestMethodInitializable) member).afterTestMethod();
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            });
        } else {
            for (TestMethodInitializable methodInitializable : methodInitializables) {
                methodInitializable.afterTestMethod();
            }
        }
        afterEachInitialized = true;
    }
}
