package com.github.vincemann.springrapid.entityrelationship.config;

import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntitySaveAdvice;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityRemoveAdvice;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityUpdateAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ServiceConfig
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class RapidBiDirServiceAdviceAutoConfiguration {

    public RapidBiDirServiceAdviceAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(BiDirEntitySaveAdvice.class)
    public BiDirEntitySaveAdvice biDirEntityPersistAdvice(CrudServiceLocator crudServiceLocator){
        return new BiDirEntitySaveAdvice(crudServiceLocator);
    }

    @Bean
    @ConditionalOnMissingBean(BiDirEntityRemoveAdvice.class)
    public BiDirEntityRemoveAdvice biDirEntityRemoveAdvice(CrudServiceLocator crudServiceLocator){
        return new BiDirEntityRemoveAdvice(crudServiceLocator);
    }

    @Bean
    @ConditionalOnMissingBean(BiDirEntityUpdateAdvice.class)
    public BiDirEntityUpdateAdvice biDirEntityUpdateAdvice(CrudServiceLocator crudServiceLocator){
        return new BiDirEntityUpdateAdvice(crudServiceLocator);
    }

}
