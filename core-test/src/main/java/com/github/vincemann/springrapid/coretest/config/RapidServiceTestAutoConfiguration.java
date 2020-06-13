package com.github.vincemann.springrapid.coretest.config;

import com.github.vincemann.springrapid.core.config.CrudServiceLocatorAutoConfiguration;
import com.github.vincemann.springrapid.core.slicing.config.ServiceTestConfig;
import com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholderResolver;
import com.github.vincemann.springrapid.coretest.service.resolve.RapidEntityPlaceholderResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@ServiceTestConfig
@Import(CrudServiceLocatorAutoConfiguration.class)
public class RapidServiceTestAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(EntityPlaceholderResolver.class)
    public EntityPlaceholderResolver entityPlaceholderResolver(){
        return new RapidEntityPlaceholderResolver();
    }

}
