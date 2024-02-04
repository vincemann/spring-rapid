package com.github.vincemann.springrapid.core.util;

import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CacheUtil {

    public static void addCacheNames(ConcurrentMapCacheManager cacheManager, String... additionalCacheNames){
        // Retrieve existing cache names
        Collection<String> existingCacheNames = cacheManager.getCacheNames();

        // Prepare a new list of cache names, ensuring no duplicates
        List<String> newCacheNames = new ArrayList<>(existingCacheNames);
        for (String cacheName : additionalCacheNames) {
            if (!existingCacheNames.contains(cacheName)) {
                newCacheNames.add(cacheName);
            }
        }

        // Set the updated list of cache names
        cacheManager.setCacheNames(newCacheNames);

    }
}
