package com.github.vincemann.springlemon.exceptions.config;

import com.github.vincemann.springlemon.exceptions.handlers.*;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
@Slf4j
public class LemonExceptionHandlerAutoConfiguration {

    public LemonExceptionHandlerAutoConfiguration() {
        log.info("Created");
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


    @Bean
    @ConditionalOnMissingBean(MultiErrorExceptionHandler.class)
    public MultiErrorExceptionHandler multiErrorExceptionHandler(){
        return new MultiErrorExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(WebExchangeBindExceptionHandler.class)
    public WebExchangeBindExceptionHandler webExchangeBindExceptionHandler(){
        return new WebExchangeBindExceptionHandler();
    }
}
