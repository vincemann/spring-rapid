package com.github.vincemann.springrapid.coretest.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractMvcTest implements InitializingBean
{


    protected MockMvc mvc;
    private WebApplicationContext wac;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMockMvcBuilder mvcBuilder = createMvcBuilder();
        mvc = mvcBuilder.build();
        injectMvcIntoFields();
    }



    // iterates over all mvc aware fields and sets mvc
    protected void injectMvcIntoFields(){
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            Class<?> fieldType = field.getType();
            if (MvcAware.class.isAssignableFrom(fieldType)){
                field.setAccessible(true);
                MvcAware mvcAware = (MvcAware) field.get(this);
                if (mvcAware != null)
                    mvcAware.setMvc(mvc);
            }
        });
    }


    protected DefaultMockMvcBuilder createMvcBuilder() {
        return MockMvcBuilders.webAppContextSetup(wac)
                .alwaysDo(print());
    }

    public MockMvc getMvc() {
        return mvc;
    }

    public WebApplicationContext getWac() {
        return wac;
    }

    @Autowired
    public void setWac(WebApplicationContext wac) {
        this.wac = wac;
    }
}
