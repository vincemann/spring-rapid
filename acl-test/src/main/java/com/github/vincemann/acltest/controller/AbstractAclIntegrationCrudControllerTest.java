package com.github.vincemann.acltest.controller;

import com.github.vincemann.acltest.ClearAclCacheTestExecutionListener;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.EntityLocator;

import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.coretest.controller.integration.AbstractIntegrationControllerTest;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclCache;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;


// todo this annotation can be removed: test
@TestExecutionListeners(
        value = {
                ClearAclCacheTestExecutionListener.class,
        },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Sql(scripts = "classpath:/remove-acl-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AbstractAclIntegrationCrudControllerTest<C extends GenericCrudController, T extends AbstractCrudControllerTestTemplate>
        extends AbstractIntegrationControllerTest<C, T> {

    private AclCache aclCache;

    /**
     * Call this before removing entities, so all references from acl cache to entities are gone
     * otherwise you will get db-fk-contraint exceptions
     */
    @AfterEach
    public void clearAclCache() {
        aclCache.clearCache();
    }

    @Autowired
    public void injectAclCache(AclCache aclCache) {
        this.aclCache = aclCache;
    }
}
