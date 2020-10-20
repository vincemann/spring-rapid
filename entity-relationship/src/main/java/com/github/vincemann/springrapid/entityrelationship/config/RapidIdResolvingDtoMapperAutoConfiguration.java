package com.github.vincemann.springrapid.entityrelationship.config;

import com.github.vincemann.springrapid.core.config.RapidCrudServiceLocatorAutoConfiguration;
import com.github.vincemann.springrapid.core.config.RapidDtoMapperAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategy;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import com.github.vincemann.springrapid.entityrelationship.controller.IdAwareMergeUpdateStrategy;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.EntityIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.UniDirChildIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.UniDirParentIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.BiDirChildIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.BiDirParentIdResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@WebConfig
@Slf4j
//overrides mergeUpdateStrategy
@AutoConfigureBefore(RapidDtoMapperAutoConfiguration.class)
@AutoConfigureAfter(RapidCrudServiceLocatorAutoConfiguration.class)
public class RapidIdResolvingDtoMapperAutoConfiguration {

    public RapidIdResolvingDtoMapperAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(MergeUpdateStrategy.class)
    public MergeUpdateStrategy idAwareMergeUpdateStrategy(){
        return new IdAwareMergeUpdateStrategy();
    }


    @ConditionalOnMissingBean(name = "biDiChildIdResolver")
    @Bean
    public EntityIdResolver biDiChildIdResolver(CrudServiceLocator crudServiceLocator){
        return new BiDirChildIdResolver(crudServiceLocator);
    }

    @ConditionalOnMissingBean(name = "uniDirChildIdResolver")
    @Bean
    public EntityIdResolver uniDirChildIdResolver(CrudServiceLocator crudServiceLocator){
        return new UniDirChildIdResolver(crudServiceLocator);
    }

    @ConditionalOnMissingBean(name = "biDiParentIdResolver")
    @Bean
    public EntityIdResolver biDiParentIdResolver(CrudServiceLocator crudServiceLocator){
        return new BiDirParentIdResolver(crudServiceLocator);
    }

    @ConditionalOnMissingBean(name = "uniDirParentIdResolver")
    @Bean
    public EntityIdResolver uniDirParentIdResolver(CrudServiceLocator crudServiceLocator){
        return new UniDirParentIdResolver(crudServiceLocator);
    }

    @ConditionalOnMissingBean(name = "idResolvingDtoPostProcessor")
    @Bean
    public DtoPostProcessor idResolvingDtoPostProcessor(List<EntityIdResolver> entityIdResolvers){
        return new IdResolvingDtoPostProcessor(entityIdResolvers);
    }

}
