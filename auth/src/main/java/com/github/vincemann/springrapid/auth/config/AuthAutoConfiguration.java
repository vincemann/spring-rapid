package com.github.vincemann.springrapid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.vincemann.springrapid.auth.*;
import com.github.vincemann.springrapid.auth.controller.EndpointService;
import com.github.vincemann.springrapid.auth.util.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableConfigurationProperties
public class AuthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuthPrincipalFactory.class)
    public AuthPrincipalFactory authenticatedPrincipalFactory(){
        return new AuthPrincipalFactoryImpl();
    }

    @Bean
    @ConditionalOnMissingBean(Validator.class)
    public jakarta.validation.Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }


    @Bean
    public Message messageUtils(MessageSource messageSource){
        return new Message(messageSource);
    }

    @ConditionalOnMissingBean(name = "authLongIdConverter")
    @Bean
    public IdConverter<Long> authLongIdConverter(){
        return new LongIdConverter();
    }

    @ConfigurationProperties("rapid-auth")
    @Bean
    @ConditionalOnMissingBean(name = "authProperties")
    public AuthProperties authProperties(){
        return new AuthProperties();
    }

}
