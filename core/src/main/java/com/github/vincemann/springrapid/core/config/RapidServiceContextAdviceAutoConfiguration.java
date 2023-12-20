package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextAdvice;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class RapidServiceContextAdviceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ServiceCallContextAdvice.class)
    public ServiceCallContextAdvice serviceCallContextAdvice(IdConverter<?> idConverter){
        return new ServiceCallContextAdvice(idConverter);
    }
}
