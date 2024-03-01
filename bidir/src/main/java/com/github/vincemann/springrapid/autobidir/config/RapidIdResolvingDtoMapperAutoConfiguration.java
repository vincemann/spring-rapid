package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.resolveid.*;
import com.github.vincemann.springrapid.core.config.RapidDtoAutoConfiguration;
import com.github.vincemann.springrapid.core.config.RapidServiceAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.dto.MergeUpdateStrategy;
import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.autobidir.resolveid.bidir.BiDirChildIdResolver;
import com.github.vincemann.springrapid.autobidir.resolveid.bidir.BiDirParentIdResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Configuration
@Slf4j
//overrides mergeUpdateStrategy
@AutoConfigureBefore(RapidDtoAutoConfiguration.class)
@AutoConfigureAfter(RapidServiceAutoConfiguration.class)
public class RapidIdResolvingDtoMapperAutoConfiguration {

    public RapidIdResolvingDtoMapperAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(MergeUpdateStrategy.class)
    public MergeUpdateStrategy idAwareMergeUpdateStrategy(){
        return new IdAwareMergeUpdateStrategy();
    }


    @Bean
    @ConditionalOnMissingBean(RelationalDtoManagerUtil.class)
    public RelationalDtoManagerUtil relationalDtoManager(){
        return new RelationalDtoManagerUtilImpl();
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
    public IdResolvingDtoPostProcessor idResolvingDtoPostProcessor(DelegatingEntityIdResolver delegatingEntityIdResolver){
        return new IdResolvingDtoPostProcessor(delegatingEntityIdResolver);
    }

    @Bean
    @ConditionalOnMissingBean(name = "delegatingEntityIdResolver")
    public DelegatingEntityIdResolver delegatingEntityIdResolver(List<EntityIdResolver> idResolvers){
        return new DelegatingEntityIdResolver(idResolvers);
    }

}
