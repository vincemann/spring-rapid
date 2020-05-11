package io.github.vincemann.springrapid.entityrelationship.config;

import io.github.vincemann.springrapid.core.config.DtoMapperAutoConfiguration;
import io.github.vincemann.springrapid.core.controller.dtoMapper.IdIdentifiableEntityConverter;
import io.github.vincemann.springrapid.core.controller.dtoMapper.IdentifiableEntityIdConverter;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

//@WebConfig
//@AutoConfigureBefore(DtoMapperAutoConfiguration.class)
//public class IdResolvingModelMapperAutoConfiguration {
//
//    @Autowired
//    private CrudServiceLocator crudServiceLocator;
//
//    @Bean
//    @ConditionalOnMissingBean(ModelMapper.class)
//    public ModelMapper idResolvingModelMapper(){
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
//        modelMapper.addConverter(new IdentifiableEntityIdConverter());
//        modelMapper.addConverter(new IdIdentifiableEntityConverter(crudServiceLocator));
//        return modelMapper;
//    }
//}
