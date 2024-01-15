package com.github.vincemann.acltest.controller;

import com.github.vincemann.acltest.ClearAclCacheTestExecutionListener;

import com.github.vincemann.springrapid.coretest.controller.integration.MvcIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclCache;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;


// todo double acl cache clearing?
@TestExecutionListeners(
        value = {
                ClearAclCacheTestExecutionListener.class,
        },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Sql(scripts = "classpath:/remove-acl-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class AclMvcIntegrationTest extends MvcIntegrationTest {

    private AclCache aclCache;

    /**
     * Call this before removing entities, so all references from acl cache to entities are gone
     * otherwise you will get db-fk-constraint exceptions
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
