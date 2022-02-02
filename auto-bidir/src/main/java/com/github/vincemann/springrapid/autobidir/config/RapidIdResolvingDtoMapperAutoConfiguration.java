package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.RapidRelationalDtoManager;
import com.github.vincemann.springrapid.autobidir.RelationalDtoManager;
import com.github.vincemann.springrapid.core.config.RapidCrudServiceLocatorAutoConfiguration;
import com.github.vincemann.springrapid.core.config.RapidDtoMapperAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategy;
import com.github.vincemann.springrapid.core.slicing.WebConfig;
import com.github.vincemann.springrapid.autobidir.controller.IdAwareMergeUpdateStrategy;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.EntityIdResolver;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.UniDirParentIdResolver;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.BiDirChildIdResolver;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.BiDirParentIdResolver;
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


    @Bean
    @ConditionalOnMissingBean(RelationalDtoManager.class)
    public RelationalDtoManager relationalDtoManager(){
        return new RapidRelationalDtoManager();
    }

    @ConditionalOnMissingBean(name = "biDiChildIdResolver")
    @Bean
    public BiDirChildIdResolver biDiChildIdResolver(){
        return new BiDirChildIdResolver();
    }

    @ConditionalOnMissingBean(name = "biDiParentIdResolver")
    @Bean
    public BiDirParentIdResolver biDiParentIdResolver(){
        return new BiDirParentIdResolver();
    }

    @ConditionalOnMissingBean(name = "uniDirParentIdResolver")
    @Bean
    public UniDirParentIdResolver uniDirParentIdResolver(){
        return new UniDirParentIdResolver();
    }

    @ConditionalOnMissingBean(name = "idResolvingDtoPostProcessor")
    @Bean
    public IdResolvingDtoPostProcessor idResolvingDtoPostProcessor(List<EntityIdResolver> entityIdResolvers, RelationalDtoManager relationalDtoManager){
        return new IdResolvingDtoPostProcessor(entityIdResolvers, relationalDtoManager);
    }

}
