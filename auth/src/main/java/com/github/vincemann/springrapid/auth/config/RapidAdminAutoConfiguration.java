package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.AdminInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Configuration
@ConditionalOnProperty(name = "rapid-auth.create-admins", havingValue = "true", matchIfMissing = true)
public class RapidAdminAutoConfiguration {

    public RapidAdminAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(AdminInitializer.class)
    public AdminInitializer adminInitializer(){
        return new AdminInitializer();
    }
}
