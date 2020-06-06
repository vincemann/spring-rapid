package io.github.vincemann.springrapid.coretest.config;

import io.github.vincemann.springrapid.coretest.auth.RapidMockAuthenticationTemplate;
import io.github.vincemann.springrapid.coretest.auth.RapidMockAuthenticationTemplateImpl;
import io.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.resolve.RapidEntityPlaceholderResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RapidCoreTestAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(RapidMockAuthenticationTemplate.class)
    public RapidMockAuthenticationTemplate rapidMockAuthenticationTemplate(){
        return new RapidMockAuthenticationTemplateImpl();
    }

    @Bean
    @ConditionalOnMissingBean(EntityPlaceholderResolver.class)
    public EntityPlaceholderResolver entityPlaceholderResolver(){
        return new RapidEntityPlaceholderResolver();
    }

    @Autowired
    public void configureGlobalResolver(EntityPlaceholderResolver entityPlaceholderResolver){
        GlobalEntityPlaceholderResolver.setResolver(entityPlaceholderResolver);
    }
}
