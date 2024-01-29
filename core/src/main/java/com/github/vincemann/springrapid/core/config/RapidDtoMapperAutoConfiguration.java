package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.dto.DtoPostProcessor;
import com.github.vincemann.springrapid.core.controller.dto.EntityPostProcessor;
import com.github.vincemann.springrapid.core.controller.dto.map.BasicDtoMapper;
import com.github.vincemann.springrapid.core.controller.dto.map.*;
import com.github.vincemann.springrapid.core.slicing.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@WebConfig
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

    @ConditionalOnMissingBean(name = "delegatingDtoMapper")
    @Bean
    //ordered List of DtoMappers gets injected @see @Order
    public DelegatingDtoMapper delegatingDtoMapper(List<DtoMapper> dtoMappers, List<EntityPostProcessor> dtoEntityPostProcessors, List<DtoPostProcessor> dtoPostProcessors){
        DelegatingDtoMapper delegatingDtoMapper = new DelegatingDtoMapper();
        dtoMappers.forEach(delegatingDtoMapper::registerDelegate);
        dtoEntityPostProcessors.forEach(delegatingDtoMapper::registerEntityPostProcessor);
        dtoPostProcessors.forEach(delegatingDtoMapper::registerEntityDtoPostProcessor);
        return delegatingDtoMapper;
    }



}
