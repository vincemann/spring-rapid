package com.github.vincemann.springrapid.core.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.LoggingObjectMapper;
import com.github.vincemann.springrapid.core.controller.json.JsonDtoPropertyValidator;
import com.github.vincemann.springrapid.core.controller.json.JsonMapper;
import com.github.vincemann.springrapid.core.controller.json.RapidJsonDtoPropertyValidator;
import com.github.vincemann.springrapid.core.controller.json.RapidJsonMapper;
import com.github.vincemann.springrapid.core.slicing.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY;

@WebConfig
@Slf4j
@EnableWebMvc
//string to json and vice versa
public class RapidJsonAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private CoreProperties coreProperties;

    public RapidJsonAutoConfiguration() {

    }

    @Override
    public void configureContentNegotiation (ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.valueOf(coreProperties.getController().getMediaType()));
    }

    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean
    public ObjectMapper objectMapper(){
        log.debug("Created Rapid Logging JsonMapper.");
        ObjectMapper mapper= new LoggingObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(ACCEPT_SINGLE_VALUE_AS_ARRAY,true);
        mapper.configure(MapperFeature.USE_STATIC_TYPING,true);
        //otherwise actuator fails sometimes
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        return mapper;
    }

    @ConditionalOnMissingBean(JsonDtoPropertyValidator.class)
    @Bean
    public JsonDtoPropertyValidator jsonDtoPropertyValidator(){
        return new RapidJsonDtoPropertyValidator();
    }

    @ConditionalOnMissingBean(JsonMapper.class)
    @Bean
    public JsonMapper jsonMapper(){
        return new RapidJsonMapper();
    }

}
