package com.github.vincemann.springrapid.sync.config;

import com.github.vincemann.springrapid.sync.AuditCollectionAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RapidSyncAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "auditAdvice")
    public AuditCollectionAdvice auditAdvice(){
        return new AuditCollectionAdvice();
    }
}
