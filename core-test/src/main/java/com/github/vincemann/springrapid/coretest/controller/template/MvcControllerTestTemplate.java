package com.github.vincemann.springrapid.coretest.controller.template;

import com.github.vincemann.springrapid.coretest.MvcAware;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@Getter
public abstract class MvcControllerTestTemplate<C> implements MvcAware {
    protected C controller;
    protected MockMvc mvc;

    @Autowired
    public void injectController(C controller) {
        this.controller = controller;
    }

    @Override
    public void setMvc(MockMvc mvc) {
        this.mvc = mvc;
    }



}
