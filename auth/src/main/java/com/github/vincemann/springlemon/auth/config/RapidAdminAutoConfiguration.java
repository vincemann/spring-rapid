package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.bootstrap.AdminInitializer;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
@Slf4j
public class RapidAdminAutoConfiguration {

    public RapidAdminAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(AdminInitializer.class)
    public AdminInitializer adminInitializer(){
        return new AdminInitializer();
    }
}
