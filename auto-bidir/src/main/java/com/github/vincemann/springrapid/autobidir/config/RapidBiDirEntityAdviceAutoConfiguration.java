package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.RapidRelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.RapidRelationalEntityManagerUtil;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.autobidir.advice.RelationalEntityAdvice;
import com.github.vincemann.springrapid.autobidir.advice.RelationalServiceUpdateAdvice;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ServiceConfig
@Configuration
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

    int count = 0;
    @Bean
    @ConditionalOnMissingBean(RelationalServiceUpdateAdvice.class)
    public RelationalServiceUpdateAdvice relationalServiceUpdateAdvice(){
        System.err.println("creating relational service update advice " + count++);
        return new RelationalServiceUpdateAdvice();
    }

    int count2 = 0;
    @Bean
    @ConditionalOnMissingBean(RelationalEntityAdvice.class)
    public RelationalEntityAdvice relationalEntityAdvice(){

        System.err.println("creating relational entity  advice " + count2++);
        return new RelationalEntityAdvice();
    }


    @Bean
    @ConditionalOnMissingBean(RelationalEntityManager.class)
    public RelationalEntityManager relationalEntityManager(){
        return new RapidRelationalEntityManager();
    }

}
