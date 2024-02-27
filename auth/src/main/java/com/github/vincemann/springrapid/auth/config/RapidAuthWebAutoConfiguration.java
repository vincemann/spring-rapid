package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.controller.UserDtoPostProcessor;
import com.github.vincemann.springrapid.auth.controller.owner.AuthOwnerLocator;
import com.github.vincemann.springrapid.auth.controller.owner.UserOwnerLocator;
import com.github.vincemann.springrapid.auth.handler.*;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.core.config.RapidCrudControllerAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

//we want to override the OwnerLocator
@AutoConfigureBefore({RapidCrudControllerAutoConfiguration.class})
public class RapidAuthWebAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(UserDtoPostProcessor.class)
    public UserDtoPostProcessor userDtoPostProcessor(){
        return new UserDtoPostProcessor();
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
