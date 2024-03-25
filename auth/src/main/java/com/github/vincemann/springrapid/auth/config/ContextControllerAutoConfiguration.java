package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.controller.ContextController;
import com.github.vincemann.springrapid.auth.service.ContextService;
import com.github.vincemann.springrapid.auth.service.ContextServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "rapid-auth.expose-context", havingValue = "true", matchIfMissing = true)
public class ContextControllerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "contextController")
    public ContextController contextController(){
        return new ContextController();
    }

    @Bean
    @ConditionalOnMissingBean(ContextService.class)
    public ContextService contextService(){
        return new ContextServiceImpl();
    }
}