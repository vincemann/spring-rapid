package com.github.vincemann.springrapid.coretest;

import org.springframework.test.web.servlet.MockMvc;

public interface MvcAware {
    public void setMvc(MockMvc mockMvc);
}
