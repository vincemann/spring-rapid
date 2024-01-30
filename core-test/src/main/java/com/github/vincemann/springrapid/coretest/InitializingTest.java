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
 * {@link TestInitializable}.
 */
@Slf4j
public abstract class InitializingTest {

    private List<TestMethodInitializable> beforeEach = new ArrayList<>();
    private List<TestMethodInitializable> afterEach = new ArrayList<>();
    private boolean beforeEachInitialized = false;
    private boolean afterEachInitialized = false;


    @Transactional
    @BeforeEach
    public void callBeforeEach() {
        if (!beforeEachInitialized) {
            ReflectionUtils.doWithFields(this.getClass(), field -> {
                ReflectionUtils.makeAccessible(field);
                Object member = field.get(this);
                if (member instanceof TestInitializable) {
                    ((TestInitializable) member).before();
                }
                if (member instanceof TestMethodInitializable) {
                    beforeEach.add((TestMethodInitializable) member);
                    ((TestMethodInitializable) member).before();
                }
            });
        } else {
            beforeEach.forEach(TestInitializable::before);
        }
        beforeEachInitialized = true;
    }

    @Transactional
    @AfterEach
    public void callAfterEach(){
        if (!afterEachInitialized) {
            ReflectionUtils.doWithFields(this.getClass(), field -> {
                ReflectionUtils.makeAccessible(field);
                Object member = field.get(this);
                if (member instanceof TestInitializable) {
                    ((TestInitializable) member).after();
                }
                if (member instanceof TestMethodInitializable) {
                    afterEach.add((TestMethodInitializable) member);
                    ((TestMethodInitializable) member).after();
                }
            });
        } else {
            afterEach.forEach(TestInitializable::after);
        }
        afterEachInitialized = true;
    }
}
