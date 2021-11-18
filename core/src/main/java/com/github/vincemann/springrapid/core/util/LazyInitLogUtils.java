package com.github.vincemann.springrapid.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.LazyInitializationException;

import java.lang.reflect.Field;
import java.util.Collection;

@Slf4j
public class LazyInitLogUtils {

    public static String toString(Object object, Boolean... ignoreLazys){
        if (object == null){
            return "null";
        }
        Boolean ignoreLazy = Boolean.TRUE;
        if (ignoreLazys.length >= 1){
            ignoreLazy = ignoreLazys[0];
        }

        Boolean finalIgnoreLazy = ignoreLazy;
        return (new ReflectionToStringBuilder(object) {
            protected Object getValue(Field f) throws IllegalAccessException {
                if (Collection.class.isAssignableFrom(f.getType())) {
                    // it is a collection
                    try {
                        // need to query element to trigger Exception
                        Collection<?> collection = (Collection<?>) f.get(object);
                        if(collection != null){
                            if (collection.size() > 0){
                                collection.stream().findFirst().get();
                            }
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (LazyInitializationException e) {
                        log.trace(e.getMessage());
                        log.warn("Could not log hibernate lazy collection field: " + f.getName() + ", skipping.");
                        log.warn("Use @LogInteractions transactional flag to load all lazy collections for logging");
                        if (finalIgnoreLazy){
                            return "[ LazyInitializationException ]";
                        }else {
                            throw e;
                        }
                    }
                }
                return super.getValue(f);
            }
        }).toString();


    }

//    public static String toString(Object object, Boolean... ignoreLazys){
//        Boolean ignoreLazy = Boolean.TRUE;
//        if (ignoreLazys.length >= 1){
//            ignoreLazy = ignoreLazys[0];
//        }
//
//        Boolean finalIgnoreLazy = ignoreLazy;
//        return (new ReflectionToStringBuilder(object) {
//            protected boolean accept(Field f) {
//                if (!super.accept(f)) {
//                    return false;
//                }
//                if (Collection.class.isAssignableFrom(f.getType())) {
//                    // it is a collection
//                    try {
//                        // need to query element to trigger Exception
//                        Collection<?> collection = (Collection<?>) f.get(object);
//                        if (collection.size() > 0){
//                            collection.stream().findFirst().get();
//                        }
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    } catch (LazyInitializationException e) {
//                        log.trace(e.getMessage());
//                        log.warn("Could not log hibernate lazy collection field: " + f.getName() + ", skipping.");
//                        log.warn("Use @LogInteractions transactional flag to load all lazy collections for logging");
//                        if (finalIgnoreLazy){
//                            return false;
//                        }else {
//                            return true;
//                        }
//                    }
//                }
//                return true;
//            }
//        }).toString();
//
//
//    }
}
