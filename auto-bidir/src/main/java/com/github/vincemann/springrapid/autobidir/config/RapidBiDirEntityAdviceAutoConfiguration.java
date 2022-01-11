package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.autobidir.advice.RelationalEntityAdvice;
import com.github.vincemann.springrapid.autobidir.advice.RelationalServiceUpdateAdvice;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ServiceConfig
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class RapidBiDirEntityAdviceAutoConfiguration  {

    public RapidBiDirEntityAdviceAutoConfiguration() {

    }

//    @Bean
//    @ConditionalOnMissingBean(BiDirEntitySaveAdvice.class)
//    public BiDirEntitySaveAdvice biDirEntityPersistAdvice(CrudServiceLocator crudServiceLocator, RelationalEntityManagerUtil relationalEntityManagerUtil){
//        return new BiDirEntitySaveAdvice(crudServiceLocator, relationalEntityManagerUtil);
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(BiDirEntityRemoveAdvice.class)
//    public BiDirEntityRemoveAdvice biDirEntityRemoveAdvice(CrudServiceLocator crudServiceLocator, RelationalEntityManagerUtil relationalEntityManagerUtil){
//        return new BiDirEntityRemoveAdvice(crudServiceLocator, relationalEntityManagerUtil);
//    }
//
    @Bean
    @ConditionalOnMissingBean(RelationalServiceUpdateAdvice.class)
    public RelationalServiceUpdateAdvice biDirEntityUpdateAdvice(){
        return new RelationalServiceUpdateAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(RelationalEntityAdvice.class)
    public RelationalEntityAdvice biDirEntityPersistAdvice(){
        return new RelationalEntityAdvice();
    }

}
