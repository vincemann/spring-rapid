package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.dto.map.PrincipalFactory;
import com.github.vincemann.springrapid.core.controller.dto.map.PrincipalFactoryImpl;
import com.github.vincemann.springrapid.core.sec.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RapidSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(RapidSecurityContext.class)
    public RapidSecurityContext rapidSecurityContext(){
        return new RapidSecurityContextImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PrincipalFactory.class)
    public PrincipalFactory principalFactory(){
        return new PrincipalFactoryImpl();
    }
}
