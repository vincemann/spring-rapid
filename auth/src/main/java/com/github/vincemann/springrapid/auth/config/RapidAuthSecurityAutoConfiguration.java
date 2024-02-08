package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.sec.*;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.JwtAuthorizationTokenService;
import com.github.vincemann.springrapid.auth.sec.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.auth.sec.AuthenticatedPrincipalFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RapidAuthSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(AuthorizationTokenService.class)
    public AuthorizationTokenService authorizationTokenService(){
        return new JwtAuthorizationTokenService();
    }


    // there can only be ONE Factory
    // if user wishes to create AuthPrincipal differently or with diff subtypes he can define own bean
    @Bean
    @ConditionalOnMissingBean(AuthenticatedPrincipalFactory.class)
    public AuthenticatedPrincipalFactory authenticatedPrincipalFactory(){
        return new AuthenticatedPrincipalFactoryImpl();
    }


    @Bean
    @ConditionalOnMissingBean(JwtPrincipalConverter.class)
    public JwtPrincipalConverter jwtClaimsPrincipalConverter(){
        return new JwtPrincipalConverterImpl();
    }
}
