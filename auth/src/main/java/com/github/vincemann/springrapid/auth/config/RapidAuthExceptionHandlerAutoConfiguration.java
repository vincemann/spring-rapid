package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.handler.*;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.core.slicing.WebConfig;
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
    @ConditionalOnMissingBean(AlreadyRegisteredException.class)
    public AlreadyRegisteredExceptionHandler alreadyRegisteredExceptionHandler(){
        return new AlreadyRegisteredExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(BadTokenExceptionHandler.class)
    public BadTokenExceptionHandler badTokenExceptionHandler(){
        return new BadTokenExceptionHandler();
    }
}
