package com.github.vincemann.springrapid.entityrelationship.config;

import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityPersistAdvice;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityRemoveAdvice;
import com.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityUpdateAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ServiceConfig
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class BiDirServiceAdviceAutoConfiguration {

    public BiDirServiceAdviceAutoConfiguration() {
        log.info("Created");
    }

    @Bean
    @ConditionalOnMissingBean(BiDirEntityPersistAdvice.class)
    public BiDirEntityPersistAdvice biDirEntityPersistAdvice(){
        return new BiDirEntityPersistAdvice();
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
