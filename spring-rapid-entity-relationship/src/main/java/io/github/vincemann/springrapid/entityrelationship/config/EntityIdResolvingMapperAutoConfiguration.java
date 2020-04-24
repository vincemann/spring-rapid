package io.github.vincemann.springrapid.entityrelationship.config;

import io.github.vincemann.springrapid.core.config.DtoMapperAutoConfiguration;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DelegatingDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.EntityIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.IdResolvingDtoMapper;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.BiDirChildIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.BiDirParentIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.UniDirChildIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.UniDirParentIdResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

@WebConfig
@AutoConfigureBefore(DtoMapperAutoConfiguration.class)
@Slf4j
public class EntityIdResolvingMapperAutoConfiguration {

    public EntityIdResolvingMapperAutoConfiguration() {
        log.info("Created");
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

    @ConditionalOnMissingBean(name = "idResolvingDtoMapper")
    @Bean
    public DtoMapper idResolvingDtoMapper(List<EntityIdResolver> entityIdResolvers){
        return new IdResolvingDtoMapper(entityIdResolvers);
    }
}
