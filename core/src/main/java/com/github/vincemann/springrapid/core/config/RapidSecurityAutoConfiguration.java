package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.security.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RapidSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(RapidSecurityContext.class)
    public RapidSecurityContext<AuthenticatedPrincipalImpl> rapidSecurityContext(){
        return new RapidSecurityContextImpl();
    }
}
