package com.github.vincemann.springrapid.coretest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes all members of test that impl {@link TestMethodInitializable}s and/or
 * {@link TestClassInitializable}.
 * Executes within spring transaction.
 *
 * Reflection depth = 1
 */
public class InitializingTestExecutionListener extends TransactionalTestExecutionListener {

    @Autowired
    private List<TestMethodInitializable> methodInitializables = new ArrayList<>();
    private boolean beforeEachInitialized = false;
    private boolean afterEachInitialized = false;


    @Transactional
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

    @Transactional
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
