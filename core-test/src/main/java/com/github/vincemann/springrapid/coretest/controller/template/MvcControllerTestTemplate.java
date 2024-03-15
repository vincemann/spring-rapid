package com.github.vincemann.springrapid.coretest.controller.template;

import com.github.vincemann.springrapid.coretest.controller.MvcAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class MvcControllerTestTemplate<C> implements MvcAware {
    protected C controller;
    protected MockMvc mvc;

    @Autowired
    public void setController(C controller) {
        this.controller = controller;
    }

    @Override
    public void setMvc(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return mvc.perform(requestBuilder);
    }

    public ResultActions perform2xx(RequestBuilder requestBuilder) throws Exception {
        return mvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());
    }

    public C getController() {
        return controller;
    }

    public MockMvc getMvc() {
        return mvc;
    }
}
