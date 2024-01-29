package com.github.vincemann.springrapid.sync.config;

import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.sync.service.ext.AuditCollectionsExtension;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@Configuration
public class RapidSyncAutoConfiguration {


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = "auditCollectionsExtension")
    public AuditCollectionsExtension auditCollectionsExtension(){
        return new AuditCollectionsExtension();
    }

}
