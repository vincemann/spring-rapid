package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LazyLogUtils {


    static class LazyLogger {
        private Object object;
        private Boolean ignoreEntitiesAndCollections = Boolean.TRUE;
        private Boolean idOnly = Boolean.FALSE;
        private Boolean ignoreLazy = Boolean.TRUE;
        private HashSet<String> propertyWhiteList = new HashSet<>();
        private Integer maxCollectionEntries = 3;
        // todo add flag for not loading new lazy entities in transaction


        @Builder
        public LazyLogger(Object object, Boolean ignoreEntitiesAndCollections, Boolean idOnly, Boolean ignoreLazy, HashSet<String> propertyWhiteList, Integer maxCollectionEntries) {
            this.object = object;
            if (ignoreEntitiesAndCollections != null)
                this.ignoreEntitiesAndCollections = ignoreEntitiesAndCollections;
            if (idOnly != null)
                this.idOnly = idOnly;
            if (ignoreLazy != null)
                this.ignoreLazy = ignoreLazy;
            if (propertyWhiteList != null)
                this.propertyWhiteList = propertyWhiteList;
            if (maxCollectionEntries!=null)
                this.maxCollectionEntries=maxCollectionEntries;
        }

        /**
         * reflection based toString method
         * @params ignore Entities and Collections (default = true), idOnly (default = false) -> only makes sense when first settings option is false,
         *                 only log LazyInitException (default=true)
         */
        public String toString(){
            if (object == null) {
                return "null";
            }
            Boolean _ignoreEntitiesAndCollections = ignoreEntitiesAndCollections;
            Boolean _idOnly = idOnly;
            Boolean _ignoreLazy = ignoreLazy;


            return (new ReflectionToStringBuilder(object, ToStringStyle.SHORT_PREFIX_STYLE) {
                protected Object getValue(Field f) throws IllegalAccessException {
                    boolean singleEntity = false;
                    boolean whiteListed = false;
                    if (propertyWhiteList != null){
                        whiteListed = propertyWhiteList.contains(f.getName());
                    }
                    try {
                        if (IdentifiableEntity.class.isAssignableFrom(f.getType())) {
                            if (_ignoreEntitiesAndCollections && !whiteListed) {
                                return "";
                            }

                            singleEntity = true;
                            IdentifiableEntity entity = ((IdentifiableEntity) f.get(object));
                            if (entity == null){
                                return "null";
                            }
                            if (_idOnly) {
                                return toId(entity);
                            }
                        } else if (Collection.class.isAssignableFrom(f.getType())) {
                            // it is a collection
                            if (_ignoreEntitiesAndCollections && !whiteListed) {
                                return "";
                            }
                            singleEntity = false;
                            // need to query element to trigger Exception
                            Collection<?> collection = (Collection<?>) f.get(object);
                            if (collection == null){
                                return "null";
                            }
                            // test for lazy init exception already with size call
                            if (collection.size() > 0) {
                                if (_idOnly) {
                                    String s = collectionToIdString(collection);
                                    if (!s.equals("super")) {
                                        return s;
                                    }
                                }
                            } else {
                                return "[]";
                            }
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (LazyInitializationException e) {
                        log.trace(e.getMessage());
                        if (singleEntity) {
                            log.warn("Could not log jpa lazy entity field: " + f.getName() + ", skipping.");
                            if (_ignoreLazy) {
                                return " < LazyInitializationException > ";
                            } else {
                                throw e;
                            }
                        } else {
                            log.warn("Could not log jpa lazy collection field: " + f.getName() + ", skipping.");
//                        log.warn("Use @LogInteractions transactional flag to load all lazy collections for logging");
                            if (_ignoreLazy) {
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

    }

//    public static String toString(Object object){
//
//    }
//
//    public static String toString(Object object){
//        return toString(object,new HashSet<>(),true,false,true);
//    }
//
//    public static String toString(Object object, Boolean ignoreEntitiesAndCollection){
//        return toString(object,null, ignoreEntitiesAndCollection,false,true);
//    }
//
//    public static String toString(Object object, Boolean ignoreEntitiesAndCollection, HashSet<String> whiteList){
//        return toString(object,whiteList, ignoreEntitiesAndCollection,false,true);
//    }
//
//    public static String toString(Object object, Boolean ignoreEntitiesAndCollection,Boolean idOnly){
//        return toString(object,null, ignoreEntitiesAndCollection,idOnly,true);
//    }


//    public static String toString(Object object,Set<String> whiteList, Boolean ignoreEntitiesAndCollections, Boolean idOnly, Boolean ignoreLazy) {
//
//
//    }



    private static String toId(IdentifiableEntity entity) {
        if (entity == null) {
            return "null";
        } else {
            return entity.getId() == null ? "null-id" : entity.getId().toString();
        }
    }

    private static String collectionToIdString(Collection collection) {
        if (Set.class.isAssignableFrom(collection.getClass())) {
            return collection.stream().map(e -> ((IdentifiableEntity) e).getId() == null ? "null" : ((IdentifiableEntity) e).getId().toString()).collect(Collectors.toSet()).toString();
        } else if (List.class.isAssignableFrom(collection.getClass())) {
            return collection.stream().map(e -> ((IdentifiableEntity) e).getId() == null ? "null" : ((IdentifiableEntity) e).getId().toString()).collect(Collectors.toList()).toString();
        } else {
            log.warn("unsupported collection type");
            return "super";
        }
    }

    //    private static LazyLogUtils instance;
////    private static EntityManager entityManager;
//
//    private LazyLogUtils(EntityManager entityManager) {
//        LazyLogUtils.entityManager = entityManager;
//    }
//
//    public static LazyLogUtils create(EntityManager entityManager) {
//        if (instance == null) {
//            instance = new LazyLogUtils(entityManager);
//            return instance;
//        } else {
//            throw new IllegalArgumentException("already created");
//        }
//    }
//
//    public static LazyLogUtils get() {
//        if (LazyLogUtils.instance == null) {
//            throw new IllegalArgumentException("no instance created");
//        } else {
//            return instance;
//        }
//    }

//    public static String getObjectDescription(Object o) {
//        if (o instanceof HibernateProxy) {
//            LazyInitializer initializer = ((HibernateProxy) o)
//                    .getHibernateLazyInitializer();
//            return initializer.getEntityName()
//                    + "#" + initializer.getIdentifier();
//        }
//        return o.toString();
//    }
//
//    public static boolean canBeUsed(Object o) {
//        if (o instanceof HibernateProxy) {
//            LazyInitializer initializer = ((HibernateProxy) o)
//                    .getHibernateLazyInitializer();
//            // if already initialized - use it!
//            if (!initializer.isUninitialized())
//                return true;
//            // if the session still works - use it!
//            return initializer.getSession() != null
//                    && initializer.getSession().isOpen();
//        }
//        return true;
//    }

//    private boolean isAttachedToTransaction(Object entity) {
//        if (!isTransaction()) {
//            return false;
//        }
//        // transactional
//        return entityManager.contains(entity);
//    }
//
//    private boolean isTransaction() {
//        return TransactionSynchronizationManager.isActualTransactionActive();
//    }

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
