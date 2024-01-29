package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.model.AuthAuthenticatedPrincipalImpl;
import com.github.vincemann.springrapid.auth.sec.*;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.JwtAuthorizationTokenServiceImpl;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RapidAuthSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(AuthorizationTokenService.class)
    public AuthorizationTokenService<AuthAuthenticatedPrincipalImpl> authorizationTokenService(){
        return new JwtAuthorizationTokenServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(RapidSecurityContext.class)
    public RapidSecurityContext<?> rapidSecurityContext(){
        return new RapidAuthSecurityContext();
    }


    // there can only be ONE Factory
    // if user wishes to create AuthPrincipal differently or with diff subtypes he can define own bean
    @Bean
    @ConditionalOnMissingBean(AuthenticatedPrincipalFactory.class)
    public AuthenticatedPrincipalFactory<?,?> authenticatedPrincipalFactory(){
        return new AuthAuthenticatedPrincipalFactory<>();
    }


    @Bean
    @ConditionalOnMissingBean(JwtClaimsToPrincipalConverter.class)
    public JwtClaimsToPrincipalConverter<AuthAuthenticatedPrincipalImpl> jwtClaimsPrincipalConverter(){
        return new JwtClaimsToPrincipalConverterImpl();
    }

    @Bean
    @ConditionalOnMissingBean(AuthSecurityContextChecker.class)
    public AuthSecurityContextChecker rapidAuthSecurityContextChecker(){
        return new AuthSecurityContextChecker();
    }
}
