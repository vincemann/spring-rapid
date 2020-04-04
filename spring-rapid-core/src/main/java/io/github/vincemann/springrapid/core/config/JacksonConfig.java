package io.github.vincemann.springrapid.core.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.vincemann.springrapid.core.config.layers.config.WebConfig;
import io.github.vincemann.springrapid.core.controller.dtoMapper.LoggingObjectMapper;
import org.springframework.context.annotation.Bean;

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
