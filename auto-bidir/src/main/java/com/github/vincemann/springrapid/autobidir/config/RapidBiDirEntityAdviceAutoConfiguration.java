package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.RapidRelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.advice.BiDirEntityAdvice;
import com.github.vincemann.springrapid.autobidir.advice.RelationalServiceUpdateAdvice;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class RapidBiDirEntityAdviceAutoConfiguration  {

    public RapidBiDirEntityAdviceAutoConfiguration() {

    }


    @Bean
    @ConditionalOnMissingBean(RelationalServiceUpdateAdvice.class)
    public RelationalServiceUpdateAdvice relationalServiceUpdateAdvice(){
        return new RelationalServiceUpdateAdvice();
    }


    @Bean
    @ConditionalOnMissingBean(BiDirEntityAdvice.class)
    public BiDirEntityAdvice biDirEntityAdvice(){
        return new BiDirEntityAdvice();
    }


    @Bean
    @ConditionalOnMissingBean(RelationalEntityManager.class)
    public RelationalEntityManager relationalEntityManager(){
        return new RapidRelationalEntityManager();
    }

}
