package io.github.vincemann.springrapid.entityrelationship.config;

import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import io.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityPersistAdvice;
import io.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityRemoveAdvice;
import io.github.vincemann.springrapid.entityrelationship.advice.BiDirEntityUpdateAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ServiceConfig
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class BiDirServiceAdviceAutoConfiguration {

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
