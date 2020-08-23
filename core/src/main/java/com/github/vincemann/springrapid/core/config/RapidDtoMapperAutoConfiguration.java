package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.dtoMapper.BasicDtoMapper;
import com.github.vincemann.springrapid.core.controller.dtoMapper.DelegatingDtoMapper;
import com.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import com.github.vincemann.springrapid.core.controller.dtoMapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@WebConfig
@Slf4j
public class RapidDtoMapperAutoConfiguration {

    public RapidDtoMapperAutoConfiguration() {
        log.info("Created");
    }

    @ConditionalOnMissingBean(name = "defaultDtoMapper")
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
    }

    @Bean
    @ConditionalOnMissingBean(ModelMapper.class)
    public ModelMapper basicModelMapper(){
        return new ModelMapper();
    }

    @ConditionalOnMissingBean(name = "delegatingDtoMapper")
    @Bean
    //ordered List of DtoMappers gets injected @see @Order
    public DelegatingDtoMapper delegatingDtoMapper(List<DtoMapper> dtoMappers, List<DtoPostProcessor> postProcessors){
        DelegatingDtoMapper delegatingDtoMapper = new DelegatingDtoMapper();
        dtoMappers.forEach(delegatingDtoMapper::registerDelegate);
        postProcessors.forEach(delegatingDtoMapper::registerPostProcessor);
        return delegatingDtoMapper;
    }



}
