package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.handler.RapidAuthenticationSuccessHandler;
import com.github.vincemann.springrapid.auth.sec.bruteforce.BruteForceAuthenticationFailureListener;
import com.github.vincemann.springrapid.auth.sec.bruteforce.BruteForceAuthenticationSuccessEventListener;
import com.github.vincemann.springrapid.auth.sec.bruteforce.LoginAttemptService;
import com.github.vincemann.springrapid.auth.sec.bruteforce.LoginAttemptServiceImpl;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@Slf4j
//we need httpTokenService
@AutoConfigureAfter(RapidAuthControllerAutoConfiguration.class)
public class RapidAuthenticationAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(name = "loginAttemptService")
    public LoginAttemptService loginAttemptService(){
        return new LoginAttemptServiceImpl();
    }


    @ConditionalOnMissingBean(name = "bruteForceAuthenticationFailureListener")
    @Bean
    public BruteForceAuthenticationFailureListener bruteForceAuthenticationFailureListener(){
        return new BruteForceAuthenticationFailureListener();
    }

    @ConditionalOnMissingBean(name = "bruteForceAuthenticationSuccessEventListener")
    @Bean
    public BruteForceAuthenticationSuccessEventListener bruteForceAuthenticationSuccessEventListener(){
        return new BruteForceAuthenticationSuccessEventListener();
    }
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
    public RapidWebSecurityConfig rapidWebSecurityConfig() {
        return new RapidWebSecurityConfig();
    }

}
