package com.github.vincemann.springrapid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.vincemann.springrapid.auth.*;
import com.github.vincemann.springrapid.core.service.EndpointService;
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

    // there can only be ONE Factory
    // if user wishes to create AuthPrincipal differently or with diff subtypes he can define own bean
    @Bean
    @ConditionalOnMissingBean(AuthPrincipalFactory.class)
    public AuthPrincipalFactory authenticatedPrincipalFactory(){
        return new AuthPrincipalFactoryImpl();
    }

    @Bean
    @ConditionalOnMissingBean(Validator.class)
    public javax.validation.Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @ConditionalOnMissingBean(EndpointService.class)
    @Bean
    public EndpointService endpointService(RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new EndpointService(requestMappingHandlerMapping);
    }

    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper= new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public Message messageUtils(MessageSource messageSource){
        return new Message(messageSource);
    }

    @ConditionalOnMissingBean(name = "idConverter")
    @Bean
    public IdConverter<Long> idConverter(){
        return new LongIdConverter();
    }

    @ConfigurationProperties("rapid-auth")
    @Bean
    @ConditionalOnMissingBean(name = "authProperties")
    public AuthProperties authProperties(){
        return new AuthProperties();
    }

}
