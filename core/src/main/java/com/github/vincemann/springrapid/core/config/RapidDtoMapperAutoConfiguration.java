package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.dto.mapper.*;
import com.github.vincemann.springrapid.core.slicing.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@WebConfig
@Slf4j
public class RapidDtoMapperAutoConfiguration {

    public RapidDtoMapperAutoConfiguration() {

    }

//    // no conditional on missing bean bc multiple diff type builders must coexist
//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    public CrudDtoMappingContextBuilder dtoMappingContextBuilder(){
//        return new CrudDtoMappingContextBuilder();
//    }

    //  HOW TO MAP, WHEN DTO CLASS IS FOUND

    @ConditionalOnMissingBean(name = "defaultDtoMapper")
    @Bean
    public DtoMapper defaultDtoMapper(/*ModelMapper modelMapper*/){
        return new BasicDtoMapper();
        // todo could set systemwide modelmapper here or use @Autowired(required=false) and dont provide modelmapper-bean in this config
//        BasicDtoMapper mapper = new BasicDtoMapper();
//        mapper.createPermanentModelMapper(modelMapper);
    }

    @Bean
    @ConditionalOnMissingBean(ModelMapper.class)
    public ModelMapper basicModelMapper(){
        return new ModelMapper();
    }

    @ConditionalOnMissingBean(name = "delegatingDtoMapper")
    @Bean
    //ordered List of DtoMappers gets injected @see @Order
    public DelegatingDtoMapper delegatingDtoMapper(List<DtoMapper> dtoMappers, List<DtoEntityPostProcessor> dtoEntityPostProcessors, List<EntityDtoPostProcessor> entityDtoPostProcessors){
        DelegatingDtoMapper delegatingDtoMapper = new DelegatingDtoMapper();
        dtoMappers.forEach(delegatingDtoMapper::registerDelegate);
        dtoEntityPostProcessors.forEach(delegatingDtoMapper::registerDtoEntityPostProcessor);
        entityDtoPostProcessors.forEach(delegatingDtoMapper::registerEntityDtoPostProcessor);
        return delegatingDtoMapper;
    }



}
