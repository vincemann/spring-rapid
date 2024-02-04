package com.github.vincemann.springrapid.sync.config;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.sync.AnnotationEntityMappingCollector;
import com.github.vincemann.springrapid.sync.EntityMappingCollector;
import com.github.vincemann.springrapid.sync.SyncProperties;
import com.github.vincemann.springrapid.sync.service.AuditLogService;
import com.github.vincemann.springrapid.sync.service.AuditLogServiceImpl;
import com.github.vincemann.springrapid.sync.util.ReflectionPropertyMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    @Bean
    @ConditionalOnMissingBean(ReflectionPropertyMatcher.class)
    public ReflectionPropertyMatcher reflectionPropertyMatcher(){
        return new ReflectionPropertyMatcher();
    }

    @ConfigurationProperties(prefix="rapid-sync")
    @ConditionalOnMissingBean(SyncProperties.class)
    @Bean
    public SyncProperties authProperties() {
        return new SyncProperties();
    }

    @Bean
    @ConditionalOnMissingBean(EntityMappingCollector.class)
    public EntityMappingCollector entityMappingCollector(){
        return new AnnotationEntityMappingCollector();
    }

    @Bean
    @ConditionalOnMissingBean(AuditLogService.class)
    public AuditLogService auditLogService(){
        return new AuditLogServiceImpl();
    }

    @Autowired
    public void collectMappings(EntityMappingCollector entityMappingCollector){
        entityMappingCollector.collectEntityToDtoMappings();
    }

}
