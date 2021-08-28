package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.RapidRelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class RapidAutoBiDirAutoConfiguration implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(Lists.newArrayList("entityRelationTypes", "dtoRelationTypes", "resolvers"));
    }

    // is needed for web and service profiles
    @Bean
    @ConditionalOnMissingBean(RelationalEntityManager.class)
    public RelationalEntityManager relationalEntityManager(){
        return new RapidRelationalEntityManager();
    }


}
