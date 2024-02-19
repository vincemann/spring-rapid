package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.service.ctx.AuthContextService;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.config.RapidCrudControllerAutoConfiguration;
import com.github.vincemann.springrapid.core.config.RapidGeneralAutoConfiguration;
import com.github.vincemann.springrapid.core.service.ctx.ContextService;
import com.github.vincemann.springrapid.core.service.ctx.CoreContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableConfigurationProperties
@AutoConfigureBefore(RapidGeneralAutoConfiguration.class)
public class RapidAuthGeneralAutoConfiguration {

    @ConfigurationProperties(prefix="rapid-auth")
    @ConditionalOnMissingBean(AuthProperties.class)
    @Bean
    public AuthProperties authProperties(CoreProperties coreProperties) {
        return new AuthProperties(coreProperties);
    }


    @Bean
    @ConditionalOnMissingBean(ContextService.class)
    public ContextService contextService(){
        return new AuthContextService();
    }

}
