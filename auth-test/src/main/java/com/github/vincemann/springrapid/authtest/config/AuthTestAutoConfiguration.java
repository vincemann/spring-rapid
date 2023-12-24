package com.github.vincemann.springrapid.authtest.config;

import com.github.vincemann.springrapid.authtest.AuthStaticDependencyInitializer;
import com.github.vincemann.springrapid.coretest.StaticDependencyInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AuthTestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(StaticDependencyInitializer.class)
    public StaticDependencyInitializer staticDependencyInitializer(){
        return new AuthStaticDependencyInitializer();
    }
}
