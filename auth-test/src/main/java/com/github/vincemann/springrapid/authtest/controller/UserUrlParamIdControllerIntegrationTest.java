package com.github.vincemann.springrapid.authtest.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.authtest.controller.template.UrlParamIdUserControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.integration.IntegrationControllerTest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

public class UserUrlParamIdControllerIntegrationTest
        <C extends AbstractUserController<?,Id,?>, Id extends Serializable>
                extends IntegrationControllerTest<C, Id, UrlParamIdUserControllerTestTemplate<C,Id>> {

    public MockHttpServletRequestBuilder signup(Object dto) throws Exception{
        return getTestTemplate().signup(dto);
    }

    @Override
    public UrlParamIdUserControllerTestTemplate<C, Id> createTestTemplate() {
        return new UrlParamIdUserControllerTestTemplate<>(getController());
    }


}
