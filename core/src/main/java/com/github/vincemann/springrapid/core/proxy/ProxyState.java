package com.github.vincemann.springrapid.core.proxy;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
/**
 *
 * Used to store state of any kind between {@link AbstractServiceExtension}'s and Services, linked by {@link AbstractExtensionServiceProxy}.
 *
 * Use wisely, dont forget to clear values after using them
 * use getAndClear when possible
 * needs to manually be cleaned!
 *
 */
public class ProxyState {
    public static Map<Thread,Map<String, Object>> STATE = new HashMap<>();

    public static void clear(){
        STATE.remove(Thread.currentThread());
    }

    public static void clear(String key){
        Map<String, Object> state = STATE.get(Thread.currentThread());
        if (state==null){
            throw new IllegalArgumentException("nothing to clear");
        }else {
            Object value = state.get(key);
            if (value==null){
                log.warn("key: "+ key + " has value null already on clearing");
            }else {
                state.remove(key);
                if (state.isEmpty()){
                    clear();
                }
            }
        }
    }

    public static void setValue(String key, Object value){
        Map<String, Object> state = STATE.get(Thread.currentThread());
        if (state==null){
            Map<String, Object> firstState = new HashMap<>();
            firstState.put(key,value);
            STATE.put(Thread.currentThread(),firstState);
        }else {
            state.put(key,value);
        }
    }

    public static <T> T get(String key){
        return (T) STATE.get(Thread.currentThread()).get(key);
    }

    public static <T> T getAndClear(String key){
        T value = (T) STATE.get(Thread.currentThread()).get(key);
        clear(key);
        return value;
    }
}
