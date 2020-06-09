package com.github.vincemann.springrapid.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Slf4j
public class ReflectionCacheAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(ReflectionUtilsBean.class)
    public ReflectionUtilsBean reflectionUtilsBean(){
        return new ReflectionUtilsBean();
    }

    @Autowired
    public void configureReflectionUtilsBean(ReflectionUtilsBean reflectionUtilsBean){
        ReflectionUtilsBean.initialize(reflectionUtilsBean);
    }


    @Bean
    @ConditionalOnMissingBean(name = "reflectionCacheManager")
    public CacheManager reflectionCacheManager() {
        log.info("Simple Reflection CacheManager registered.");
        return new ConcurrentMapCacheManager("reflections");
    }


}
