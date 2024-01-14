package com.github.vincemann.springrapid.coretest.controller.integration;

import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.coretest.controller.AbstractMvcTest;
import com.github.vincemann.springrapid.coretest.controller.automock.AbstractAutoMockCrudControllerTest;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import org.springframework.test.context.ActiveProfiles;


/**
 * Integration test pendant to {@link AbstractAutoMockCrudControllerTest}.
 * {@link org.springframework.test.web.servlet.MockMvc} is still used.
 */
@ActiveProfiles(value = {
        RapidTestProfiles.TEST, RapidProfiles.WEB, RapidTestProfiles.WEB_TEST,
        RapidProfiles.SERVICE,RapidTestProfiles.SERVICE_TEST})
public abstract class MvcIntegrationTest extends AbstractMvcTest
{

}
