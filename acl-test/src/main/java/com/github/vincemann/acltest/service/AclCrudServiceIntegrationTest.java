package com.github.vincemann.acltest.service;


import com.github.vincemann.acltest.ClearAclCacheTestExecutionListener;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.service.CrudServiceIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.security.acls.model.AclCache;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;

import java.io.Serializable;

/**
 * Configures Context, Database and TestExecutionListeners for ServiceIntegrationTests.
 */
// dont do via TestExecutionLister bc aclCache must be cleared before any remove operations are done to
// avoid fk-constraint-issues
// if this is not needed, the aclCache still gets automatically cleared after testMethod via @AfterEach annotation
//@TestExecutionListeners(
//        value = {
//                ClearAclCacheTestExecutionListener.class,
//        },
//        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
//)
@Sql(scripts = "classpath:/remove-acl-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AclCrudServiceIntegrationTest<
        S extends CrudService<E, Id>,
        E extends IdentifiableEntity<Id>,
        Id extends Serializable
        >
        extends CrudServiceIntegrationTest<S, E, Id> {

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
