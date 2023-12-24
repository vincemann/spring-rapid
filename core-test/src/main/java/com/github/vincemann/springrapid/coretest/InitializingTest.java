package com.github.vincemann.springrapid.coretest;

import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import com.github.vincemann.springrapid.core.util.JpaUtils;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.coretest.bootstrap.DatabaseInitializerTestExecutionListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseClass for Tests that wish to initialize members that implement {@link TestMethodInitializable}s and/or {@link TestContextAware} and/or
 * {@link TestInitializable}.
 */
@Slf4j
@TestExecutionListeners(value = DatabaseInitializerTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class InitializingTest {

    private List<TestMethodInitializable> beforeEachMethodInitializables = new ArrayList<>();
    private List<TestMethodInitializable> afterEachMethodInitializables = new ArrayList<>();
    private boolean init = false;
    private boolean afterInit = false;

    @Autowired(required = false)
    private EntityManager entityManager;

    @Autowired(required = false)
    private CrudServiceLocator crudServiceLocator;

    // beans from currently cached context are injected into this class before each test
    @BeforeEach
    public void keepStaticDependenciesInSyncWithCachedContext(){
        // beans might be null when only web layer is tested
        if (crudServiceLocator != null){
            EntityLocator.setCrudServiceLocator(crudServiceLocator);
        }
        if (entityManager != null){
            JpaUtils.setEntityManager(entityManager);
            LazyToStringUtil.setEntityManager(entityManager);
        }
    }

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
                if (member instanceof TestContextAware) {
                    ((TestContextAware) member).setTestContext(this);
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
