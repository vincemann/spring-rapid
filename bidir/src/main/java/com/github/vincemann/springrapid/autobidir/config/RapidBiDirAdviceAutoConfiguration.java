package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerImpl;
import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.advice.RelationalEntityAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class RapidBiDirAdviceAutoConfiguration {

    public RapidBiDirAdviceAutoConfiguration() {

    }


    @Bean
    @ConditionalOnMissingBean(RelationalEntityAdvice.class)
    public RelationalEntityAdvice biDirEntityAdvice(){
        return new RelationalEntityAdvice();
    }


    @Bean
    @ConditionalOnMissingBean(RelationalEntityManager.class)
    public RelationalEntityManager relationalEntityManager(){
        return new RelationalEntityManagerImpl();
    }

}
