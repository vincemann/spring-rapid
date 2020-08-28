package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.bootstrap.AdminInitializer;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

@ServiceConfig
@Slf4j
public class LemonAdminAutoConfiguration {

    public LemonAdminAutoConfiguration() {
        log.info("Created");
    }

    @Bean
    @ConditionalOnMissingBean(AdminInitializer.class)
    public AdminInitializer adminDatabaseDataInitializer(
            LemonService<?,?,?> lemonService,
            UserDetailsService userDetailsService,
            LemonProperties lemonProperties){
        return new AdminInitializer(lemonService,userDetailsService,lemonProperties);
    }
}
