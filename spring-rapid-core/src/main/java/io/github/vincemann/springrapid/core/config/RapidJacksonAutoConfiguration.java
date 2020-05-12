package io.github.vincemann.springrapid.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import io.github.vincemann.springrapid.core.controller.dtoMapper.LoggingObjectMapper;
import io.github.vincemann.springrapid.core.util.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY;

@WebConfig
@Slf4j
public class RapidJacksonAutoConfiguration {

    public RapidJacksonAutoConfiguration() {
        log.info("Created");
    }

    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean
    public ObjectMapper objectMapper(){
        log.debug("Created Rapid Logging JsonMapper.");
        ObjectMapper mapper= new LoggingObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(ACCEPT_SINGLE_VALUE_AS_ARRAY,true);
        mapper.configure(MapperFeature.USE_STATIC_TYPING,true);
        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean(MapperUtils.class)
    public MapperUtils mapperUtils(ObjectMapper objectMapper){
        return new MapperUtils(objectMapper);
    }

}
