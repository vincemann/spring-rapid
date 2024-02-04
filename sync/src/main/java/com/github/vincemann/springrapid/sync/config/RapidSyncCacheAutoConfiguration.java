package com.github.vincemann.springrapid.sync.config;

import com.github.vincemann.springrapid.core.util.CacheUtil;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class RapidSyncCacheAutoConfiguration implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        CacheUtil.addCacheNames(cacheManager,"matchingPropertiesCache");
    }


}
