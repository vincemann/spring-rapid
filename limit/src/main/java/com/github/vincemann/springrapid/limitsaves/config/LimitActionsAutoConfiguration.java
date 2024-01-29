package com.github.vincemann.springrapid.limitsaves.config;

import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.limitsaves.TooManyRequestsException;
import com.github.vincemann.springrapid.limitsaves.TooManyRequestsExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class LimitActionsAutoConfiguration {

    @ConditionalOnMissingBean(TooManyRequestsException.class)
    @Bean
    public TooManyRequestsExceptionHandler tooManyRequestsExceptionHandler(){
        return new TooManyRequestsExceptionHandler();
    }
}
