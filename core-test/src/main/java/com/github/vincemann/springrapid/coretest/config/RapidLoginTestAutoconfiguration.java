package com.github.vincemann.springrapid.coretest.config;


import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.coretest.login.MockLoginTemplate;
import com.github.vincemann.springrapid.coretest.login.RapidMockLoginTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RapidLoginTestAutoconfiguration {

    @ConditionalOnMissingBean(name = "mockLoginTemplate")
    @Bean
    public MockLoginTemplate<RapidAuthenticatedPrincipal> mockLoginTemplate(){
        return new RapidMockLoginTemplate();
    }
}
