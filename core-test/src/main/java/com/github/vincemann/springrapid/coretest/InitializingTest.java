package com.github.vincemann.springrapid.coretest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseClass for Tests that wish to initialize members that implement {@link TestMethodInitializable}s and/or
 * {@link TestClassInitializable}.
 */
@Slf4j
public abstract class InitializingTest {

    private List<TestMethodInitializable> methodInitializables = new ArrayList<>();
    private boolean beforeEachInitialized = false;
    private boolean afterEachInitialized = false;


    @Transactional
    @BeforeEach
    public void callBeforeEach() throws Exception{
        if (!beforeEachInitialized) {
            ReflectionUtils.doWithFields(this.getClass(), field -> {
                try {
                    ReflectionUtils.makeAccessible(field);
                    Object member = field.get(this);
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

    @Transactional
    @AfterEach
    public void callAfterEach() throws Exception {
        if (!afterEachInitialized) {
            ReflectionUtils.doWithFields(this.getClass(), field -> {
                try {
                    ReflectionUtils.makeAccessible(field);
                    Object member = field.get(this);
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
