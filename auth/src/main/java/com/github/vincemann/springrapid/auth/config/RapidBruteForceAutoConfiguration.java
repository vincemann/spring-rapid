package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.sec.bruteforce.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    @ConditionalOnProperty(name = "rapid-auth.bruteForceProtection", havingValue = "true")
public class RapidBruteForceAutoConfiguration extends SecurityConfigurerAdapter {


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

    @Bean
    @ConditionalOnMissingBean(LoginAttemptService.class)
    public LoginAttemptService loginAttemptService(){
        return new LoginAttemptServiceImpl();
    }

}
