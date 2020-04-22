package com.naturalprogrammer.spring.lemon.auth.config;

import com.naturalprogrammer.spring.lemon.auth.handler.AccessDeniedExceptionHandler;
import com.naturalprogrammer.spring.lemon.auth.handler.BadCredentialsExceptionHandler;
import com.naturalprogrammer.spring.lemon.auth.handler.JsonPatchExceptionHandler;
import com.naturalprogrammer.spring.lemon.auth.handler.UsernameNotFoundExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.AccessDeniedException;

@Configuration
public class LemonAuthExceptionHandlerAutoConfiguration {

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
    @ConditionalOnMissingBean(JsonPatchExceptionHandler.class)
    public JsonPatchExceptionHandler jsonPatchExceptionHandler(){
        return new JsonPatchExceptionHandler();
    }
}
