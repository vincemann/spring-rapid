package io.github.spring.lemon.auth.config;

import io.github.spring.lemon.auth.handler.AccessDeniedExceptionHandler;
import io.github.spring.lemon.auth.handler.BadCredentialsExceptionHandler;
import io.github.spring.lemon.auth.handler.UsernameNotFoundExceptionHandler;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
@Slf4j
public class LemonAuthExceptionHandlerAutoConfiguration {

    public LemonAuthExceptionHandlerAutoConfiguration() {
        log.info("Created");
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
}
