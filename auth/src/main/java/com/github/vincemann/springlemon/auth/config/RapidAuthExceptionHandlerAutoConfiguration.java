package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.handler.AccessDeniedExceptionHandler;
import com.github.vincemann.springlemon.auth.handler.BadCredentialsExceptionHandler;
import com.github.vincemann.springlemon.auth.handler.BadTokenExceptionHandler;
import com.github.vincemann.springlemon.auth.handler.UsernameNotFoundExceptionHandler;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
@Slf4j
public class RapidAuthExceptionHandlerAutoConfiguration {

    public RapidAuthExceptionHandlerAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(UsernameNotFoundExceptionHandler.class)
    public UsernameNotFoundExceptionHandler usernameNotFoundExceptionHandler(){
        return new UsernameNotFoundExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedExceptionHandler.class)
    public AccessDeniedExceptionHandler accessDeniedExceptionHandler(){
        return new AccessDeniedExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(BadCredentialsExceptionHandler.class)
    public BadCredentialsExceptionHandler badCredentialsExceptionHandler(){
        return new BadCredentialsExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(BadTokenExceptionHandler.class)
    public BadTokenExceptionHandler badTokenExceptionHandler(){
        return new BadTokenExceptionHandler();
    }
}
