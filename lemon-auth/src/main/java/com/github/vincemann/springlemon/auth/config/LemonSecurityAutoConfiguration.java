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


    @Bean
    @ConditionalOnMissingBean(PrincipalUserConverter.class)
    public PrincipalUserConverter<LemonAuthenticatedPrincipal, AbstractUser<?>> principalUserConverter(){
        return new LemonPrincipalUserConverter();
    }


    @Bean
    @ConditionalOnMissingBean(JwtClaimsPrincipalConverter.class)
    public JwtClaimsPrincipalConverter<LemonAuthenticatedPrincipal> jwtClaimsPrincipalConverter(){
        return new LemonJwtClaimsPrincipalConverter();
    }
}
