package io.github.vincemann.springrapid.entityrelationship.config;

import io.github.vincemann.springrapid.core.config.DtoMapperAutoConfiguration;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoPostProcessor;
import io.github.vincemann.springrapid.core.controller.rapid.mergeUpdate.MergeUpdateStrategy;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import io.github.vincemann.springrapid.entityrelationship.controller.IdAwareMergeUpdateStrategy;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.EntityIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.UniDirChildIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.UniDirParentIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.BiDirChildIdResolver;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.BiDirParentIdResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@WebConfig
@Slf4j
@AutoConfigureBefore(DtoMapperAutoConfiguration.class)
public class IdResolvingDtoMapperAutoConfiguration {

    public IdResolvingDtoMapperAutoConfiguration() {
        log.info("Created");
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
