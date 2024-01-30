package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.model.AuthAuthenticatedPrincipal;
import com.github.vincemann.springrapid.auth.sec.*;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.JwtAuthorizationTokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RapidAuthSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(AuthorizationTokenService.class)
    public AuthorizationTokenService<AuthAuthenticatedPrincipal> authorizationTokenService(){
        return new JwtAuthorizationTokenServiceImpl();
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
    public JwtPrincipalConverter<AuthAuthenticatedPrincipal> jwtClaimsPrincipalConverter(){
        return new JwtPrincipalConverterImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AuthSecurityContextChecker.class)
    public AuthSecurityContextChecker rapidAuthSecurityContextChecker(){
        return new AuthSecurityContextChecker();
    }
}
