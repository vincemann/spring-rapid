package com.github.vincemann.springrapid.authtest.controller.template;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.coretest.controller.template.ControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.template.UrlParamIdCrudControllerTestTemplate;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UrlParamIdUserControllerTestTemplate
        <C extends AbstractUserController<?, Id, ?>, Id extends Serializable>
        extends UrlParamIdCrudControllerTestTemplate<C,Id> {

    public UrlParamIdUserControllerTestTemplate(C controller) {
        super(controller);
    }

    public MockHttpServletRequestBuilder signup(Object dto) throws Exception {
        return post(getController().getAuthProperties().getController().getSignupUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }

    // todo add more methods there for each endpoint
}
