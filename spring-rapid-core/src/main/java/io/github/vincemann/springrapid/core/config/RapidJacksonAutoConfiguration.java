package io.github.vincemann.springrapid.core.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import io.github.vincemann.springrapid.core.controller.dtoMapper.LoggingObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RapidJacksonAutoConfiguration {

    public RapidJacksonAutoConfiguration() {
        log.info("Created");
    }

    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper= new LoggingObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.getDeserializationConfig().with(MapperFeature.USE_STATIC_TYPING);
        return mapper;
    }

}