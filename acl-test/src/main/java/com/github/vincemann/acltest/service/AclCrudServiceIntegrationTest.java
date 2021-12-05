package com.github.vincemann.acltest.service;


import com.github.vincemann.acltest.ClearAclCacheTestExecutionListener;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.service.CrudServiceIntegrationTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;

import java.io.Serializable;

/**
 * Configures Context, Database and TestExecutionListeners for ServiceIntegrationTests.
 */
@TestExecutionListeners(
        value = {
                ClearAclCacheTestExecutionListener.class,
        },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Sql(scripts = "classpath:/remove-acl-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AclCrudServiceIntegrationTest<
        S extends CrudService<E,Id>,
        E extends IdentifiableEntity<Id>,
        Id extends Serializable
        >
        extends CrudServiceIntegrationTest<S,E,Id> {
}
