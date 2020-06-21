package com.github.vincemann.springrapid.coretest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
public abstract class InitializingTest {

    private List<BeforeEachMethodInitializable> beforeEachMethodInitializables = new ArrayList<>();
    private boolean init = false;

    @BeforeEach
    public void setup() throws Exception {
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
