package com.github.vincemann.springrapid.coretest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.TestExecutionListeners;
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

    private List<TestMethodInitializable> beforeEachMethodInitializables = new ArrayList<>();
    private List<TestMethodInitializable> afterEachMethodInitializables = new ArrayList<>();
    private boolean init = false;
    private boolean afterInit = false;


    @Transactional
    @BeforeEach
    public void callBeforeInitializables() throws Exception {
        if (!init) {
            ReflectionUtils.doWithFields(this.getClass(), field -> {
                ReflectionUtils.makeAccessible(field);
                Object member = field.get(this);
                if (member instanceof TestInitializable) {
                    ((TestInitializable) member).before();
                }
                if (member instanceof TestMethodInitializable) {
                    beforeEachMethodInitializables.add((TestMethodInitializable) member);
                    ((TestMethodInitializable) member).before();
                }
            });
        } else {
            beforeEachMethodInitializables.forEach(TestInitializable::before);
        }
        init = true;
    }

    @Transactional
    @AfterEach
    public void callAfterInitializables(){
        if (!afterInit) {
            ReflectionUtils.doWithFields(this.getClass(), field -> {
                ReflectionUtils.makeAccessible(field);
                Object member = field.get(this);
                if (member instanceof TestInitializable) {
                    ((TestInitializable) member).after();
                }
                if (member instanceof TestMethodInitializable) {
                    afterEachMethodInitializables.add((TestMethodInitializable) member);
                    ((TestMethodInitializable) member).after();
                }
            });
        } else {
            afterEachMethodInitializables.forEach(TestInitializable::after);
        }
        afterInit = true;
    }
}
