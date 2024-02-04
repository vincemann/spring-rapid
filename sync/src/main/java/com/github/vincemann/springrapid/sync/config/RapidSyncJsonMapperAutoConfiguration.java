package com.github.vincemann.springrapid.sync.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.vincemann.springrapid.core.controller.json.JsonMapper;
import com.github.vincemann.springrapid.sync.controller.DtoClassDeserializer;
import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.sync.controller.SyncStatusDeserializer;
import com.github.vincemann.springrapid.sync.controller.SyncStatusSerializer;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class RapidSyncJsonMapperAutoConfiguration {

    @Autowired
    public void configureObjectMapper(JsonMapper jsonMapper){
        ObjectMapper mapper = jsonMapper.getObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(SyncStatus.class, new SyncStatusDeserializer());
        module.addSerializer(SyncStatus.class, new SyncStatusSerializer());
        mapper.registerModule(module);
    }
}
