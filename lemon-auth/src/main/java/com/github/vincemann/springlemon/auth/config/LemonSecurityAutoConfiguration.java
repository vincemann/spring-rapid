package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.security.*;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LemonSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(RapidSecurityContext.class)
    public RapidSecurityContext<?> rapidSecurityContext(){
        return new LemonSecurityContext();
    }


    // there can only be ONE Factory
    // if user wishes to create AuthPrincipal differently or with diff subtypes he can define own bean
    @Bean
    @ConditionalOnMissingBean(AuthenticatedPrincipalFactory.class)
    public AuthenticatedPrincipalFactory<LemonAuthenticatedPrincipal, AbstractUser<?>> lemonAuthPrincipalAbstractUserConverter(){
        return new LemonAuthenticatedPrincipalFactory<>();
    }


    @Bean
    @ConditionalOnMissingBean(JwtClaimsPrincipalConverter.class)
    public JwtClaimsPrincipalConverter<LemonAuthenticatedPrincipal> jwtClaimsPrincipalConverter(){
        return new LemonJwtClaimsPrincipalConverter();
    }

    @Bean
    @ConditionalOnMissingBean(LemonSecurityContextChecker.class)
    public LemonSecurityContextChecker lemonSecurityContextChecker(){
        return new LemonSecurityContextChecker();
    }
}
