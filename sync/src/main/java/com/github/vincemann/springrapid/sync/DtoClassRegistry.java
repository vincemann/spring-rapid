package com.github.vincemann.springrapid.sync;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class DtoClassRegistry {

    @Getter
    private Map<String,Class<?>> entries = new HashMap<>();

    @Getter
    private Class<?> fallback;

    public void register(String key, Class<?> dtoClass){
        entries.put(key,dtoClass);
    }

    public void registerFallback(Class<?> dtoClass){
        this.fallback = dtoClass;
    }

    public Class<?> find(String key){
        return entries.get(key);
    }

}
