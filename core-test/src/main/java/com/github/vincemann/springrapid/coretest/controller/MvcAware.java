package com.github.vincemann.springrapid.coretest.controller;

import org.springframework.test.web.servlet.MockMvc;

public interface MvcAware {
    public void setMvc(MockMvc mockMvc);
}
