package com.github.vincemann.springrapid.sync.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.vincemann.springrapid.sync.controller.SyncStatusDeserializer;
import com.github.vincemann.springrapid.sync.controller.SyncStatusSerializer;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RapidSyncObjectMapperAutoConfiguration {

    @Autowired
    public void configureObjectMapper(ObjectMapper mapper){
        SimpleModule module = new SimpleModule();
        module.addDeserializer(SyncStatus.class, new SyncStatusDeserializer());
        module.addSerializer(SyncStatus.class, new SyncStatusSerializer());
        mapper.registerModule(module);
    }
}
