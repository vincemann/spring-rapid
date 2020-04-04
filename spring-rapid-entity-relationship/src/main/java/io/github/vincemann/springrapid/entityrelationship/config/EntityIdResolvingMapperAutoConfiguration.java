package io.github.vincemann.springrapid.entityrelationship.config;

import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DelegatingFallbackToDefaultDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.EntityIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.IdResolvingDtoMapper;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.BiDirChildIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.BiDirParentIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.UniDirChildIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.UniDirParentIdResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

@WebConfig
public class EntityIdResolvingMapperAutoConfiguration {

    @Bean
    public EntityIdResolver biDiChildIdResolver(CrudServiceLocator crudServiceLocator){
        return new BiDirChildIdResolver(crudServiceLocator);
    }

    @Bean
    public EntityIdResolver uniDirChildIdResolver(CrudServiceLocator crudServiceLocator){
        return new UniDirChildIdResolver(crudServiceLocator);
    }

    @Bean
    public EntityIdResolver biDiParentIdResolver(CrudServiceLocator crudServiceLocator){
        return new BiDirParentIdResolver(crudServiceLocator);
    }

    @Bean
    public EntityIdResolver uniDirParentIdResolver(CrudServiceLocator crudServiceLocator){
        return new UniDirParentIdResolver(crudServiceLocator);
    }

    @Primary
    @Bean
    public DtoMapper dtoMapper(List<EntityIdResolver> entityIdResolvers, @Qualifier("default") DtoMapper defaultMapper){
        DelegatingFallbackToDefaultDtoMapper delegatingFallbackToDefaultDtoMapper = new DelegatingFallbackToDefaultDtoMapper(defaultMapper);
        delegatingFallbackToDefaultDtoMapper.registerDelegate(new IdResolvingDtoMapper(entityIdResolvers));
        return delegatingFallbackToDefaultDtoMapper;
    }
}
