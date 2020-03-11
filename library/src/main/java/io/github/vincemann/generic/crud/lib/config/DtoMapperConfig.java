package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DelegatingFallbackToDefaultDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.IdResolvingDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.BiDirChildIdResolver;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.BiDirParentIdResolver;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.UniDirChildIdResolver;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.UniDirParentIdResolver;
import io.github.vincemann.generic.crud.lib.service.locator.CrudServiceLocator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;

import java.util.List;

@Configuration
@Import(CrudServiceLocatorConfig.class)
@WebLayer
public class DtoMapperConfig {

    @Qualifier("default")
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
    }

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
    public DtoMapper dtoMapper(List<EntityIdResolver> entityIdResolvers){
        DelegatingFallbackToDefaultDtoMapper delegatingFallbackToDefaultDtoMapper = new DelegatingFallbackToDefaultDtoMapper(defaultDtoMapper());
        delegatingFallbackToDefaultDtoMapper.registerDelegate(new IdResolvingDtoMapper(entityIdResolvers));
        return delegatingFallbackToDefaultDtoMapper;
    }


}
