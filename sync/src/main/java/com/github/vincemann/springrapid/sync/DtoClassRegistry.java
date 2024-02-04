package com.github.vincemann.springrapid.sync;

import java.util.HashMap;
import java.util.Map;

public class DtoClassRegistry {

    private Map<String,Class<?>> registry = new HashMap<>();
    private Class<?> fallback;

    public void register(String key, Class<?> dtoClass){
        registry.put(key,dtoClass);
    }

    public void registerFallback(Class<?> dtoClass){
        this.fallback = dtoClass;
    }

    public Class<?> find(String key){
        return registry.get(key);
    }
}
