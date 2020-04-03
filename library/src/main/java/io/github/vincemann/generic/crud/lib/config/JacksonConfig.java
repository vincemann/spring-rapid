package io.github.vincemann.generic.crud.lib.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.vincemann.generic.crud.lib.config.layers.component.WebComponent;
import io.github.vincemann.generic.crud.lib.config.layers.config.WebConfig;
import io.github.vincemann.generic.crud.lib.controller.LoggingObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@WebConfig
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper= new LoggingObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.getDeserializationConfig().with(MapperFeature.USE_STATIC_TYPING);
        return mapper;
    }

}
