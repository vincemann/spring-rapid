package com.github.vincemann.springrapid.authtest.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.coretest.controller.template.ControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UserControllerTestTemplate
        <C extends AbstractUserController<?, Id, ?>, Id extends Serializable>
        extends ControllerTestTemplate<C> {

    public UserControllerTestTemplate(C controller) {
        super(controller);
    }

    public MockHttpServletRequestBuilder signup(Object dto) throws Exception {
        return post(getController().getAuthProperties().getController().getSignupUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }
}
