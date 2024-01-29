package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.handler.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class RapidExceptionHandlerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WebExchangeBindExceptionHandler.class)
    public WebExchangeBindExceptionHandler webExchangeBindExceptionHandler(){
        return new WebExchangeBindExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(ConstraintViolationExceptionHandler.class)
    public ConstraintViolationExceptionHandler constraintViolationExceptionHandler(){
        return new ConstraintViolationExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(JsonPatchExceptionHandler.class)
    public JsonPatchExceptionHandler jsonPatchExceptionHandler(){
        return new JsonPatchExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(BadEntityExceptionHandler.class)
    public BadEntityExceptionHandler badEntityExceptionHandler(){
        return new BadEntityExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(EntityNotFoundExceptionHandler.class)
    public EntityNotFoundExceptionHandler entityNotFoundExceptionHandler() {
        return new EntityNotFoundExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(JsonParseExceptionHandler.class)
    public JsonParseExceptionHandler jsonParseExceptionHandler(){
        return new JsonParseExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(JsonProcessingExceptionHandler.class)
    public JsonProcessingExceptionHandler jsonProcessingExceptionHandler(){
        return new JsonProcessingExceptionHandler();
    }
}
