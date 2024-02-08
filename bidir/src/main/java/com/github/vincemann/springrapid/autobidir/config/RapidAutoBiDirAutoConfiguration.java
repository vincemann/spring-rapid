package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtilImpl;
import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.core.util.CacheUtil;
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
        CacheUtil.addCacheNames(cacheManager,"entityRelationTypesCache","dtoRelationTypesCache");
    }


    @Bean
    @ConditionalOnMissingBean(RelationalEntityManagerUtil.class)
    public RelationalEntityManagerUtil relationalEntityManagerUtil(){
        return new RelationalEntityManagerUtilImpl();
    }


}
