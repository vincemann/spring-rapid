package io.github.vincemann.springrapid.coretest.config;

import io.github.vincemann.springrapid.coretest.auth.RapidMockAuthenticationTemplate;
import io.github.vincemann.springrapid.coretest.auth.RapidMockAuthenticationTemplateImpl;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.RapidEntityPlaceholderResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

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
}
