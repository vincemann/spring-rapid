package com.github.vincemann.springrapid.coretest.config;

import com.github.vincemann.springrapid.coretest.auth.RapidMockAuthenticationTemplate;
import com.github.vincemann.springrapid.coretest.auth.RapidMockAuthenticationTemplateImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RapidTestAutoConfiguration {



    @Bean
    @ConditionalOnMissingBean(RapidMockAuthenticationTemplate.class)
    public RapidMockAuthenticationTemplate rapidMockAuthenticationTemplate(){
        return new RapidMockAuthenticationTemplateImpl();
    }

}
