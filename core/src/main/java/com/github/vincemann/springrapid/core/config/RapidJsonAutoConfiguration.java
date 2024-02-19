package com.github.vincemann.springrapid.core.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.json.JsonDtoPropertyValidator;
import com.github.vincemann.springrapid.core.controller.json.JsonMapper;
import com.github.vincemann.springrapid.core.controller.json.JsonDtoPropertyValidatorImpl;
import com.github.vincemann.springrapid.core.controller.json.JsonMapperImpl;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY;

@Configuration
@Slf4j
@EnableWebMvc
public class RapidJsonAutoConfiguration implements WebMvcConfigurer {

    public RapidJsonAutoConfiguration() {

    }

    @Override
    public void configureContentNegotiation (ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean
    public ObjectMapper objectMapper(){
        if (log.isDebugEnabled())
            log.debug("Created Rapid Logging JsonMapper.");
        ObjectMapper mapper= new ObjectMapper();
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
        return new JsonDtoPropertyValidatorImpl();
    }

    @ConditionalOnMissingBean(JsonMapper.class)
    @Bean
    public JsonMapper jsonMapper(){
        return new JsonMapperImpl();
    }

}
