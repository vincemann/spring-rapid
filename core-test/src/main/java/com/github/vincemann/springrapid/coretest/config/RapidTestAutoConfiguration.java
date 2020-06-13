package com.github.vincemann.springrapid.coretest.config;

import com.github.vincemann.springrapid.core.controller.rapid.CurrentUserIdProvider;
import com.github.vincemann.springrapid.coretest.auth.RapidMockAuthenticationTemplate;
import com.github.vincemann.springrapid.coretest.auth.RapidMockAuthenticationTemplateImpl;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class RapidTestAutoConfiguration {


    @Primary
    @Bean
    public CurrentUserIdProvider currentUserIdProviderSpy(CurrentUserIdProvider currentUserIdProvider){
        return Mockito.spy(currentUserIdProvider);
    }

    @Bean
    @ConditionalOnMissingBean(RapidMockAuthenticationTemplate.class)
    public RapidMockAuthenticationTemplate rapidMockAuthenticationTemplate(){
        return new RapidMockAuthenticationTemplateImpl();
    }

}
