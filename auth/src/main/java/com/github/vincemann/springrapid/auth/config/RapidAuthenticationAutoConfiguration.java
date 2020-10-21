package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.handler.RapidAuthenticationSuccessHandler;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@WebConfig
@Slf4j
//we need httpTokenService
@AutoConfigureAfter(RapidAuthControllerAutoConfiguration.class)
public class RapidAuthenticationAutoConfiguration {



    /**
     * Configures AuthenticationSuccessHandler if missing
     */
    @Bean
    @ConditionalOnMissingBean(RapidAuthenticationSuccessHandler.class)
    public RapidAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new RapidAuthenticationSuccessHandler();
    }

    /**
     * Configures AuthenticationFailureHandler if missing
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }

    /**
     * Configures LemonSecurityConfig if missing
     */
    @Bean
    @ConditionalOnMissingBean(RapidWebSecurityConfig.class)
    public RapidWebSecurityConfig lemonWebSecurityConfig() {
        return new RapidWebSecurityConfig();
    }

}
