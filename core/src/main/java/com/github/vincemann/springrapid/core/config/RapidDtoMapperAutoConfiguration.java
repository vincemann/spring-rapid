package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.dto.DtoPostProcessor;
import com.github.vincemann.springrapid.core.controller.dto.EntityPostProcessor;
import com.github.vincemann.springrapid.core.controller.dto.map.BasicDtoMapper;
import com.github.vincemann.springrapid.core.controller.dto.map.*;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

@Configuration
@Slf4j
public class RapidDtoMapperAutoConfiguration {


    @ConditionalOnMissingBean(name = "defaultDtoMapper")
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
        // not using system wide modelmapper bc id need to synchronize the object to maintain the same config -> not performant enough
    }

//    @Bean
//    @ConditionalOnMissingBean(ModelMapper.class)
//    public ModelMapper basicModelMapper(){
//        ModelMapper mapper = new ModelMapper();
//        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        return mapper;
//    }

    @ConditionalOnMissingBean(DelegatingDtoMapper.class)
    @Bean
    public DelegatingDtoMapper delegatingDtoMapper(){
        return new DelegatingDtoMapperImpl();
    }

    @Bean
    @ConditionalOnBean(DelegatingDtoMapper.class)
    public ApplicationListener<ContextRefreshedEvent> dtoMapperRegistrar(ApplicationContext context, DelegatingDtoMapper delegatingDtoMapper) {
        return event -> {
            Map<String, DtoMapper> mappers = context.getBeansOfType(DtoMapper.class);
            Map<String, DtoPostProcessor> dtoPPs = context.getBeansOfType(DtoPostProcessor.class);
            Map<String, EntityPostProcessor> entityPPs = context.getBeansOfType(EntityPostProcessor.class);

            mappers.values().forEach(delegatingDtoMapper::register);
            dtoPPs.values().forEach(delegatingDtoMapper::registerDtoPostProcessor);
            entityPPs.values().forEach(delegatingDtoMapper::registerEntityPostProcessor);
        };
    }


}
