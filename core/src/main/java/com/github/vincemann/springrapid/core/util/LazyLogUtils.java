package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.LazyInitializationException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LazyLogUtils {


    private static String toId(IdentifiableEntity entity) {
        if (entity == null) {
            return "null";
        } else {
            return entity.getId() == null ? "null-id" : entity.getId().toString();
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

    static class LazyLogger {
        private Object object;
        private Boolean ignoreEntitiesAndCollections = Boolean.TRUE;
        private Boolean idOnly = Boolean.FALSE;
        private Boolean ignoreLazyException = Boolean.TRUE;
        private HashSet<String> propertyWhiteList = new HashSet<>();
        private Integer maxCollectionEntries = 3;
        private Boolean loadLazyEntities = Boolean.TRUE;

        private PropertyState property;


        @Builder
        public LazyLogger(Object object, Boolean ignoreEntitiesAndCollections, Boolean idOnly, Boolean ignoreLazyException, HashSet<String> propertyWhiteList, Integer maxCollectionEntries, Boolean loadLazyEntities) {
            this.object = object;
            if (ignoreEntitiesAndCollections != null)
                this.ignoreEntitiesAndCollections = ignoreEntitiesAndCollections;
            if (idOnly != null)
                this.idOnly = idOnly;
            if (ignoreLazyException != null)
                this.ignoreLazyException = ignoreLazyException;
            if (propertyWhiteList != null)
                this.propertyWhiteList = propertyWhiteList;
            if (maxCollectionEntries != null)
                this.maxCollectionEntries = maxCollectionEntries;
            if (loadLazyEntities != null)
                this.loadLazyEntities = loadLazyEntities;
        }

        /**
         * reflection based toString method
         *
         * @params ignore Entities and Collections (default = true), idOnly (default = false) -> only makes sense when first settings option is false,
         * only log LazyInitException (default=true)
         */
        public String toString() {
            if (object == null) {
                return "null";
            }


            return (new ReflectionToStringBuilder(object, ToStringStyle.SHORT_PREFIX_STYLE) {
                protected Object getValue(Field f) throws IllegalAccessException {


                    // init propertyState
                    property = new PropertyState(f);
                    property.entity = isEntity();
                    property.collection = isCollection();
                    property.ignored = ignoreEntitiesAndCollections;
                    if (isWhiteListActive() && propertyWhiteList.contains(f.getName())) {
                        property.setWhiteListed();
                    }

                    String propertyString = "super";
                    try {
                        if (property.isIgnored()) {
                            return "";
                        }
                        if (property.isEntity()) {
                            propertyString = entityToString();
                        } else if (isCollection()) {
                            propertyString = collectionToString();
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (LazyInitializationException e) {
                        return lazyInitExceptionToString(e);
                    }
                    if (propertyString.equals("super")){
                        return super.getValue(f);
                    }else {
                        throw new RuntimeException("Unhandled Property" + property);
                    }
                }
            }).toString();
        }

        protected String lazyInitExceptionToString(LazyInitializationException e) {
            log.trace(e.getMessage());
            if (property.isEntity()) {
                log.warn("Could not log jpa lazy entity field: " + property.field.getName() + ", skipping.");
                if (ignoreLazyException) {
                    return " < LazyInitializationException > ";
                } else {
                    throw e;
                }
            } else if (property.isCollection()) {
                log.warn("Could not log jpa lazy collection field: " + property.field.getName() + ", skipping.");
//                        log.warn("Use @LogInteractions transactional flag to load all lazy collections for logging");
                if (ignoreLazyException) {
                    return "[ LazyInitializationException ]";
                } else {
                    throw e;
                }
            } else {
                // this should never happen
                throw new RuntimeException("LazyInitException without entities being detected");
            }
        }

        protected Boolean isWhiteListActive() {
            return !propertyWhiteList.isEmpty();
        }

        protected Boolean isEntity() {
            return IdentifiableEntity.class.isAssignableFrom(property.field.getType());
        }

        protected Boolean isCollection() {
            return Collection.class.isAssignableFrom(property.field.getType());
        }

        protected String entityToString() throws IllegalAccessException {
            IdentifiableEntity entity = ((IdentifiableEntity) property.field.get(object));
            if (entity == null) {
                return "null";
            }
            if (idOnly) {
                return toId(entity);
            }
            return "super";
        }

        protected String collectionToString() throws IllegalAccessException {
            // it is a collection
            // need to query element to trigger Exception
            Collection<?> collection = (Collection<?>) property.field.get(object);
            if (collection == null) {
                return "null";
            }
            // test for lazy init exception already with size call
            if (collection.size() > 0) {
                if (idOnly) {
                    String s = collectionToIdString(collection);
                    if (!s.equals("super")) {
                        return s;
                    }
                }
            } else {
                return "[]";
            }
            return "super";
        }

        static class PropertyState {
            private Field field;
            private Boolean whiteListed = Boolean.FALSE;
            private Boolean entity = Boolean.FALSE;
            private Boolean collection = Boolean.FALSE;
            private Boolean ignored = Boolean.FALSE;
            private Boolean loaded = Boolean.FALSE;

            public PropertyState(Field field) {
                this.field = field;
            }

            public void setWhiteListed() {
                this.whiteListed = Boolean.TRUE;
                this.ignored = Boolean.FALSE;
            }

            public Boolean isWhiteListed() {
                return whiteListed;
            }

            public Boolean isEntity() {
                return entity;
            }

            public Boolean isCollection() {
                return collection;
            }

            public Boolean isLoaded() {
                return loaded;
            }

            /**
             * user wants to ignore entitiesAndCollections and this property is not whitelisted
             */
            public Boolean isIgnored() {
                return ignored;
            }
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
