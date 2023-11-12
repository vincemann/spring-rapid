package com.github.vincemann.springrapid.core.proxy;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * Used to store state of any kind between {@link AbstractServiceExtension}'s and Services, linked by {@link AbstractExtensionServiceProxy}.
 *
 * Use wisely, dont forget to clear values after using them
 * use {@link this#getAndClear(String)} when possible
 * needs to manually be cleaned!
 *
 */

// todo there has to be a better way to do this or avoid completely
@Slf4j
public class ProxyState {
    private final static Map<Thread,Map<String, Object>> STATE = new HashMap<>();

    

    public static void clear(){
        if (log.isTraceEnabled())
            log.trace("clearing " + Thread.currentThread());
        STATE.remove(Thread.currentThread());
        // this comment made idea realize that it should not call this method many times randomly...
//        throw new IllegalArgumentException("clearing");
    }

    public static void clear(String key){
        if (log.isTraceEnabled())
            log.trace("clearing key: " + key + " " + Thread.currentThread());
        Map<String, Object> state = STATE.get(Thread.currentThread());
        if (state==null){
//            throw new IllegalArgumentException("nothing to clear");
            log.debug("state is null");
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
        // this comment made idea realize that it should not call this method many times randomly...
//        throw new IllegalArgumentException("clearing key?");
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
        Map<String, Object> threadState = STATE.get(Thread.currentThread());
        if (threadState==null){
            return null;
        }else {
           return  (T) threadState.get(key);
        }
    }

    public static <T> T getAndClear(String key){
       T value = get(key);
       clear(key);
       return value;
    }
}
