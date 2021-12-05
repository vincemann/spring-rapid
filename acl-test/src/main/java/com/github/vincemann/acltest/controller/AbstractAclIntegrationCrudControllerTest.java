package com.github.vincemann.acltest.controller;

import com.github.vincemann.acltest.ClearAclCacheTestExecutionListener;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.controller.AbstractCrudControllerTest;
import com.github.vincemann.springrapid.coretest.controller.integration.AbstractIntegrationControllerTest;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:/remove-acl-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestExecutionListeners(
        value = {
                ClearAclCacheTestExecutionListener.class,
        },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class AbstractAclIntegrationCrudControllerTest<C extends GenericCrudController,T extends AbstractCrudControllerTestTemplate>
        extends AbstractIntegrationControllerTest<C,T> {
}
