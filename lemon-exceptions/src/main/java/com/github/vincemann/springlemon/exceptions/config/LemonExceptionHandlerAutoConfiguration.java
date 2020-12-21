package com.github.vincemann.springlemon.exceptions.config;

import com.github.vincemann.springlemon.exceptions.handlers.*;
import com.github.vincemann.springrapid.core.slicing.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
@Slf4j
public class LemonExceptionHandlerAutoConfiguration {

    public LemonExceptionHandlerAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(MultiErrorExceptionHandler.class)
    public MultiErrorExceptionHandler multiErrorExceptionHandler(){
        return new MultiErrorExceptionHandler();
    }


}
