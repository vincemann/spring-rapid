package com.github.vincemann.springrapid.coretest.config;


import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.slicing.TestConfig;
import com.github.vincemann.springrapid.coretest.slicing.WebTestConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@WebTestConfig
public class RapidControllerTestTemplateAutoconfiguration {

    @ConditionalOnMissingBean(name = "crudControllerTestTemplate")
    @Bean
    public CrudControllerTestTemplate crudControllerTestTemplate(){
        return new CrudControllerTestTemplate();
    }
}
