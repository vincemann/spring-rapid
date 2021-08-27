package com.github.vincemann.springrapid.entityrelationship.config;

import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.entityrelationship.CacheClassKeyGenerator;
import com.github.vincemann.springrapid.entityrelationship.RapidRelationalEntityManager;
import com.github.vincemann.springrapid.entityrelationship.RelationalEntityManager;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntitySaveAdvice;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityRemoveAdvice;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityUpdateAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ServiceConfig
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
@EnableCaching
public class RapidBiDirEntityAdviceAutoConfiguration implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    public RapidBiDirEntityAdviceAutoConfiguration() {

    }

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(Lists.newArrayList("entityRelationTypes", "dtoRelationTypes"));
    }

    @Bean
    @ConditionalOnMissingBean(RelationalEntityManager.class)
    public RelationalEntityManager relationalEntityManager(){
        return new RapidRelationalEntityManager();
    }

    @Bean
    @ConditionalOnMissingBean(BiDirEntitySaveAdvice.class)
    public BiDirEntitySaveAdvice biDirEntityPersistAdvice(CrudServiceLocator crudServiceLocator,RelationalEntityManager relationalEntityManager){
        return new BiDirEntitySaveAdvice(crudServiceLocator,relationalEntityManager);
    }

    @Bean
    @ConditionalOnMissingBean(BiDirEntityRemoveAdvice.class)
    public BiDirEntityRemoveAdvice biDirEntityRemoveAdvice(CrudServiceLocator crudServiceLocator,RelationalEntityManager relationalEntityManager){
        return new BiDirEntityRemoveAdvice(crudServiceLocator,relationalEntityManager);
    }

    @Bean
    @ConditionalOnMissingBean(BiDirEntityUpdateAdvice.class)
    public BiDirEntityUpdateAdvice biDirEntityUpdateAdvice(CrudServiceLocator crudServiceLocator,RelationalEntityManager relationalEntityManager){
        return new BiDirEntityUpdateAdvice(crudServiceLocator,relationalEntityManager);
    }

}
