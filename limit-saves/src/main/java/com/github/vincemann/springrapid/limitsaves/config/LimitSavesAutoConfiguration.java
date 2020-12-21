package com.github.vincemann.springrapid.limitsaves.config;

import com.github.vincemann.springrapid.core.slicing.WebConfig;
import com.github.vincemann.springrapid.limitsaves.TooManyRequestsException;
import com.github.vincemann.springrapid.limitsaves.TooManyRequestsExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
public class LimitSavesAutoConfiguration {

    @ConditionalOnMissingBean(TooManyRequestsException.class)
    @Bean
    public TooManyRequestsExceptionHandler tooManyRequestsExceptionHandler(){
        return new TooManyRequestsExceptionHandler();
    }
}
