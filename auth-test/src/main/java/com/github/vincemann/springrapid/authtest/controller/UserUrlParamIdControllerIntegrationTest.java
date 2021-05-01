package com.github.vincemann.springrapid.authtest.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.authtest.controller.template.LoginDto;
import com.github.vincemann.springrapid.authtest.controller.template.AbstractUserControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.integration.AbstractIntegrationControllerTest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UserUrlParamIdControllerIntegrationTest
        <C extends AbstractUserController<?,Id,?>, Id extends Serializable>
                extends AbstractIntegrationControllerTest<C, Id, AbstractUserControllerTestTemplate<C,Id>> {

    public MockHttpServletRequestBuilder signup(Object dto) throws Exception{
        return getTestTemplate().signup(dto);
    }

    public MockHttpServletRequestBuilder login(LoginDto loginDto){
        return getTestTemplate().login(loginDto);
    }


    @Override
    public AbstractUserControllerTestTemplate<C, Id> createTestTemplate() {
        return new AbstractUserControllerTestTemplate<>(getController());
    }



}
