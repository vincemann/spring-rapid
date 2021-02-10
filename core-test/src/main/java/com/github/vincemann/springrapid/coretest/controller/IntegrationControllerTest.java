package com.github.vincemann.springrapid.coretest.controller;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import org.springframework.test.context.ActiveProfiles;

import java.io.Serializable;


/**
 * Integration test pendant to {@link com.github.vincemann.springrapid.coretest.controller.automock.AutoMockControllerTest}.
 * {@link org.springframework.test.web.servlet.MockMvc} is still used.
 */
@ActiveProfiles(value = {
        RapidTestProfiles.TEST, RapidProfiles.WEB, RapidTestProfiles.WEB_TEST,
        RapidProfiles.SERVICE,RapidTestProfiles.SERVICE_TEST})
public abstract class IntegrationControllerTest
        <C extends GenericCrudController<?, Id, ?, ?, ?>, Id extends Serializable>
            extends AbstractMvcCrudControllerTest<C,Id>
{

}
