package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.LazyInitializationException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LazyLogUtils {


    private static LazyLogUtils instance;
    private EntityManager entityManager;

    private LazyLogUtils(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static LazyLogUtils create(EntityManager entityManager) {
        if (instance == null) {
            instance = new LazyLogUtils(entityManager);
            return instance;
        } else {
            throw new IllegalArgumentException("already created");
        }
    }

    public static LazyLogUtils get() {
        if (LazyLogUtils.instance == null) {
            throw new IllegalArgumentException("no instance created");
        } else {
            return instance;
        }
    }

    public static String toString(Object object, Boolean idOnly, Boolean... ignoreLazys) {
        Boolean ignoreLazy = Boolean.TRUE;
        if (ignoreLazys.length >= 1) {
            ignoreLazy = ignoreLazys[0];
        }
        return get()._toString(object, idOnly, ignoreLazy);
    }

    public String _toString(Object object, Boolean idOnly, Boolean ignoreLazy) {
        if (object == null) {
            return "null";
        }
//        Set<Object> alreadyVisited = new HashSet<>();
        Boolean finalIgnoreLazy = ignoreLazy;
        return (new ReflectionToStringBuilder(object) {
            protected Object getValue(Field f) throws IllegalAccessException {
                boolean singleEntity = false;
                try {
                    Object o = f.get(object);
                    if (o == null) {
                        return "null";
                    }
//                    if (alreadyVisited.contains(o)){
//                        return "-";
//                    }

                    if (IdentifiableEntity.class.isAssignableFrom(f.getType())) {
                        singleEntity = true;
                        IdentifiableEntity entity = ((IdentifiableEntity)o);
//                        alreadyVisited.add(entity);
                        if (idOnly) {
                            return convertToId(entity);
                        } else {
                            String s = convertToString(entity);
                            if (!s.equals("super")) {
                                return s;
                            }
                        }
                    } else if (Collection.class.isAssignableFrom(f.getType())) {
                        // it is a collection
                        singleEntity = false;
                        // need to query element to trigger Exception
                        Collection<?> collection = (Collection<?>) o;
//                        alreadyVisited.add(collection);
                        if (collection.size() > 0) {

                            // test for lazy init exception
                            Object entity = collection.stream().findFirst().get();
                            if (IdentifiableEntity.class.isAssignableFrom(entity.getClass())) {
                                if (isTransaction()) {
                                    if (isAttachedToTransaction(entity)) {
                                        // collection is loaded
                                        if (idOnly) {
                                            String s = collectionToIdString(collection);
                                            if (!s.equals("super")) {
                                                return s;
                                            }
                                        } else {
                                            // if super toString also uses this method, no additional entities will be loaded
//                                                    return super.getValue(f);
                                        }
                                    } else {
                                        // detached collection, dont load
                                        return "[detached...]";
                                    }
                                } else {
                                    // no transaction -> just load, lazy init exception will limit results
                                    if (idOnly) {
                                        String s = collectionToIdString(collection);
                                        if (!s.equals("super")) {
                                            return s;
                                        }
                                    } else {
//                                                return super.getValue(f);
                                    }
                                }
                                        
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (LazyInitializationException e) {
                    log.trace(e.getMessage());
                    if (singleEntity) {
                        log.warn("Could not log hibernate lazy entity field: " + f.getName() + ", skipping.");
                        if (finalIgnoreLazy) {
                            return " LazyInitializationException ";
                        } else {
                            throw e;
                        }
                    } else {
                        log.warn("Could not log hibernate lazy collection field: " + f.getName() + ", skipping.");
//                        log.warn("Use @LogInteractions transactional flag to load all lazy collections for logging");
                        if (finalIgnoreLazy) {
                            return "[ LazyInitializationException ]";
                        } else {
                            throw e;
                        }
                    }
                }
                return super.getValue(f);
            }
        }).toString();
    }

    private String collectionToIdString(Collection collection) {
        if (Set.class.isAssignableFrom(collection.getClass())) {
            return collection.stream().map(e -> ((IdentifiableEntity) e).getId() == null ? "null" : ((IdentifiableEntity) e).getId().toString()).collect(Collectors.toSet()).toString();
        } else if (List.class.isAssignableFrom(collection.getClass())) {
            return collection.stream().map(e -> ((IdentifiableEntity) e).getId() == null ? "null" : ((IdentifiableEntity) e).getId().toString()).collect(Collectors.toList()).toString();
        } else {
            log.warn("unsupported collection type");
            return "super";
        }
    }

    private String convertToString(IdentifiableEntity entity) {
        if (isTransaction()) {
            if (isAttachedToTransaction(entity)) {
                return "super";
            } else {
                return "detached";
            }
        } else {
            return "super";
        }
    }

    private String convertToId(IdentifiableEntity entity) {
        if (isTransaction()) {
            if (isAttachedToTransaction(entity)) {
                return toId(entity);
            } else {
                return "detached-id";
            }
        } else {
            return toId(entity);
        }
    }

    private String toId(IdentifiableEntity entity) {
        if (entity == null) {
            return "null-entity";
        } else {
            return entity.getId() == null ? "null-id" : entity.getId().toString();
        }
    }

    private boolean isAttachedToTransaction(Object entity) {
        if (!isTransaction()) {
            return false;
        }
        // transactional
        return entityManager.contains(entity);
    }

    private boolean isTransaction() {
        return TransactionSynchronizationManager.isActualTransactionActive();
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
