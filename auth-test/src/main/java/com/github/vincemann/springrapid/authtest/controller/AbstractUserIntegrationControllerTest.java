package com.github.vincemann.springrapid.authtest.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.authtest.controller.template.AbstractUserControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.integration.AbstractIntegrationControllerTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

public abstract class AbstractUserIntegrationControllerTest
        <C extends AbstractUserController, T extends AbstractUserControllerTestTemplate>
                extends AbstractIntegrationControllerTest<C,AbstractUserControllerTestTemplate> {

    public MockHttpServletRequestBuilder signup(Object dto) throws Exception{
        return getTestTemplate().signup(dto);
    }

    @Override
    protected DefaultMockMvcBuilder createMvcBuilder() {
        // activate, so login endpoint and security config is active
        DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
        mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
        return mvcBuilder;
    }


    public ResultActions login(String email, String password) throws Exception {
        return getTestTemplate().login(email,password);
    }

    // dont use, bc when user is saved hash will be send
//    public String login2xx(AbstractUser user) throws Exception {
//        return getTestTemplate().login2xx(user);
//    }

    public String login2xx(String email, String password) throws Exception {
        return getTestTemplate().login2xx(email,password);
    }



}
