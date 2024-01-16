package com.github.vincemann.springrapid.sync.config;

import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.sync.controller.EntitySyncStatusSerializer;
import com.github.vincemann.springrapid.sync.controller.EntitySyncStatusSerializerImpl;
import com.github.vincemann.springrapid.sync.service.AuditCollectionsExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class RapidSyncAutoConfiguration {

//    @ConditionalOnMissingBean(CustomAuditingRepository.class)
//    @Bean
//    public <E extends AuditingEntity<?>> CustomAuditingRepository<E> customAuditingRepository(EntityManager entityManager) {
//        return new RapidCustomAuditingRepository<>(entityManager);
//    }

    @Bean
    @ConditionalOnMissingBean(EntitySyncStatusSerializer.class)
    public EntitySyncStatusSerializer entitySyncStatusSerializer(){
        return new EntitySyncStatusSerializerImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "auditCollectionsExtension")
    public AuditCollectionsExtension auditCollectionsExtension(){
        return new AuditCollectionsExtension();
    }

}
