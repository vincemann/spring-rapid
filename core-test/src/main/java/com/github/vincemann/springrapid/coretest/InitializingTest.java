package com.github.vincemann.springrapid.coretest;

import com.github.vincemann.springrapid.coretest.bootstrap.DatabaseInitializerTestExecutionListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * BaseClass for Tests that wish to initialize members that implement {@link BeforeEachMethodInitializable}s and/or {@link TestContextAware} and/or
 * {@link TestInitializable}.
 */
@Slf4j
@TestExecutionListeners(value = DatabaseInitializerTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class InitializingTest {

    private List<BeforeEachMethodInitializable> beforeEachMethodInitializables = new ArrayList<>();
    private boolean init = false;

    @BeforeEach
    protected void callInitializables() throws Exception {
        runInitializables();
    }

    protected void runInitializables(){
        if (!init) {
            ReflectionUtils.doWithFields(this.getClass(), field -> {
                ReflectionUtils.makeAccessible(field);
                Object member = field.get(this);
                if (member instanceof TestInitializable) {
                    ((TestInitializable) member).init();
                }
                if (member instanceof BeforeEachMethodInitializable) {
                    beforeEachMethodInitializables.add((BeforeEachMethodInitializable) member);
                    ((BeforeEachMethodInitializable) member).init();
                }
                if (member instanceof TestContextAware) {
                    ((TestContextAware) member).setTestContext(this);
                }
            });
        } else {
            beforeEachMethodInitializables.forEach(TestInitializable::init);
        }
        init = true;
    }
}
