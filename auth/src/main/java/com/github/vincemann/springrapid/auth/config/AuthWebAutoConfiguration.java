package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.core.handler.JsonProcessingExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class AuthWebAutoConfiguration {


    /**
     * Configures AuthenticationSuccessHandler if missing
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler();
    }

    /**
     * Configures AuthenticationFailureHandler if missing
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }


    @Bean
    @ConditionalOnMissingBean(WebSecurityConfig.class)
    public WebSecurityConfig rapidWebSecurityConfig() {
        return new WebSecurityConfig();
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


    @Bean
    @ConditionalOnMissingBean(com.github.vincemann.springrapid.core.handler.WebExchangeBindExceptionHandler.class)
    public com.github.vincemann.springrapid.core.handler.WebExchangeBindExceptionHandler webExchangeBindExceptionHandler(){
        return new com.github.vincemann.springrapid.core.handler.WebExchangeBindExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(com.github.vincemann.springrapid.core.handler.ConstraintViolationExceptionHandler.class)
    public com.github.vincemann.springrapid.core.handler.ConstraintViolationExceptionHandler constraintViolationExceptionHandler(){
        return new com.github.vincemann.springrapid.core.handler.ConstraintViolationExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(com.github.vincemann.springrapid.core.handler.BadEntityExceptionHandler.class)
    public com.github.vincemann.springrapid.core.handler.BadEntityExceptionHandler badEntityExceptionHandler(){
        return new com.github.vincemann.springrapid.core.handler.BadEntityExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(com.github.vincemann.springrapid.core.handler.EntityNotFoundExceptionHandler.class)
    public com.github.vincemann.springrapid.core.handler.EntityNotFoundExceptionHandler entityNotFoundExceptionHandler() {
        return new com.github.vincemann.springrapid.core.handler.EntityNotFoundExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(com.github.vincemann.springrapid.core.handler.JsonParseExceptionHandler.class)
    public com.github.vincemann.springrapid.core.handler.JsonParseExceptionHandler jsonParseExceptionHandler(){
        return new com.github.vincemann.springrapid.core.handler.JsonParseExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(com.github.vincemann.springrapid.core.handler.JsonProcessingExceptionHandler.class)
    public com.github.vincemann.springrapid.core.handler.JsonProcessingExceptionHandler jsonProcessingExceptionHandler(){
        return new JsonProcessingExceptionHandler();
    }


}
