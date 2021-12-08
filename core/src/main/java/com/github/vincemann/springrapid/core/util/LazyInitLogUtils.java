package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.LazyInitializationException;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class LazyInitLogUtils {


    private EntityManager entityManager;
    private static LazyInitLogUtils instance;

    private LazyInitLogUtils(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static LazyInitLogUtils create(EntityManager entityManager){
        if (instance == null){
            instance = new LazyInitLogUtils(entityManager);
        }else {
            throw new IllegalArgumentException("already created");
        }
    }

    public static LazyInitLogUtils get(){
        if (LazyInitLogUtils.instance == null){
            throw new IllegalArgumentException("no instance created");
        }else {
            return instance;
        }
    }

    public static String toString(Object object, Boolean idOnly, Boolean... ignoreLazys){
        return get()._toString(object,idOnly,ignoreLazys);
    }

    public String _toString(Object object, Boolean idOnly, Boolean... ignoreLazys){
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
                try {
                        if (IdentifiableEntity.class.isAssignableFrom(f.getType())){
                            IdentifiableEntity entity = ((IdentifiableEntity)f.get(object));
                            if (isTransaction()){
                                if (isLoaded(entity)){

                                }
                            }
                            if (idOnly){
                                if (entity == null){
                                    return "null";
                                }else{
                                    return entity.getId() == null ? "null" : entity.getId().toString();
                                }
                            }
                        }
                        else if (Collection.class.isAssignableFrom(f.getType())) {
                            // it is a collection

                            // need to query element to trigger Exception
                            Collection<?> collection = (Collection<?>) f.get(object);
                            if (collection != null) {
                                if (collection.size() > 0) {
                                    // test for lazy init exception
                                    Object entity = collection.stream().findFirst().get();
                                    // only log id of entity
                                    if (idOnly) {
                                        if (IdentifiableEntity.class.isAssignableFrom(entity.getClass())) {
                                            if (Set.class.isAssignableFrom(collection.getClass())) {
                                                return collection.stream().map(e -> ((IdentifiableEntity) e).getId() == null ? "null" : ((IdentifiableEntity) e).getId().toString()).collect(Collectors.toSet());
                                            } else if (List.class.isAssignableFrom(collection.getClass())) {
                                                return collection.stream().map(e -> ((IdentifiableEntity) e).getId() == null ? "null" : ((IdentifiableEntity) e).getId().toString()).collect(Collectors.toList());
                                            } else {
                                                log.warn("unsupported collection type");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (LazyInitializationException e) {
                        log.trace(e.getMessage());
                        log.warn("Could not log hibernate lazy collection field: " + f.getName() + ", skipping.");
//                        log.warn("Use @LogInteractions transactional flag to load all lazy collections for logging");
                        if (finalIgnoreLazy){
                            return "[ LazyInitializationException ]";
                        }else {
                            throw e;
                        }
                    }
                return super.getValue(f);
            }
        }).toString();
    }

    private String convertToId(IdentifiableEntity entity){

    }

    private boolean isLoaded(Object entity){
        if (!isTransaction()){
            return false;
        }
        // transactional
        return entityManager.contains(entity);
    }

    private boolean isTransaction(){
        return this.entityManager.getTransaction() != null;
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
