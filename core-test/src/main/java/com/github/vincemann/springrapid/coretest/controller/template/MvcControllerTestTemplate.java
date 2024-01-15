package com.github.vincemann.springrapid.coretest.controller.template;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@Getter
public abstract class MvcControllerTestTemplate<C> {
    protected C controller;
    protected MockMvc mvc;

    @Autowired
    public void injectController(C controller) {
        this.controller = controller;
    }

    public void setMvc(MockMvc mvc) {
        this.mvc = mvc;
    }



}
