package com.github.vincemann.springlemon.exceptions.config;

import com.github.vincemann.springlemon.exceptions.handlers.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LemonExceptionHandlerAutoConfiguration {

    public LemonExceptionHandlerAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(MultiErrorExceptionHandler.class)
    public MultiErrorExceptionHandler multiErrorExceptionHandler(){
        return new MultiErrorExceptionHandler();
    }


}
