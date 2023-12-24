package com.github.vincemann.springrapid.coretest.config;

import com.github.vincemann.springrapid.core.util.ConditionalOtherConfig;
import com.github.vincemann.springrapid.coretest.CoreStaticDependencyInitializer;
import com.github.vincemann.springrapid.coretest.StaticDependencyInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RapidTestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(StaticDependencyInitializer.class)
    @ConditionalOtherConfig(value = "com.github.vincemann.springrapid.authtest.config.AuthTestAutoConfiguration")
    public StaticDependencyInitializer staticDependencyInitializer(){
        return new CoreStaticDependencyInitializer();
    }
}
