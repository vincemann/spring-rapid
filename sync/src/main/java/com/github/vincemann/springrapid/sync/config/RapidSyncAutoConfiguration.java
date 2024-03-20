package com.github.vincemann.springrapid.sync.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.vincemann.springrapid.sync.controller.SyncStatusDeserializer;
import com.github.vincemann.springrapid.sync.controller.SyncStatusSerializer;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.sync.model.audit.AuditorAwareImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class RapidSyncAutoConfiguration {

    @Autowired
    public void configureObjectMapper(ObjectMapper mapper){
        SimpleModule module = new SimpleModule();
        module.addDeserializer(SyncStatus.class, new SyncStatusDeserializer());
        module.addSerializer(SyncStatus.class, new SyncStatusSerializer());
        mapper.registerModule(module);
    }

    @ConditionalOnMissingBean(AuditorAware.class)
    @Bean
    public AuditorAware<Long> auditorAware(){
        return new AuditorAwareImpl<>();
    }
}
