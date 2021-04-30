package com.github.vincemann.springrapid.authtest.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.coretest.controller.MvcCrudControllerTest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public interface UserMvcControllerTest
        <C extends AbstractUserController<?, Id, ?>, Id extends Serializable>

        extends MvcCrudControllerTest<C,Id> {



    public default MockHttpServletRequestBuilder signup(Object dto) throws Exception {
        return post(getController().getAuthProperties().getController().getSignupUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }
}
