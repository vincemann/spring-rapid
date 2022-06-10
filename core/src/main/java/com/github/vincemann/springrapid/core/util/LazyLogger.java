package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.LazyInitializationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class LazyLogger {
    private static EntityManager entityManager;

    private Object parent;
    private Boolean ignoreEntitiesAndCollections = Boolean.TRUE;
    private Boolean idOnly = Boolean.FALSE;
    private Boolean ignoreLazyException = Boolean.TRUE;
    private HashSet<String> propertyWhiteList = new HashSet<>();
    private Integer maxCollectionEntries = 3;
    private Boolean loadLazyEntities = Boolean.TRUE;

    private Property property;
    private Map<Class, List<Object>> clazzParentsMap = new HashMap<>();


    @Builder
    public LazyLogger(Object rootObject, Boolean ignoreEntitiesAndCollections, Boolean idOnly, Boolean ignoreLazyException, HashSet<String> propertyWhiteList, Integer maxCollectionEntries, Boolean loadLazyEntities) {
        this.parent = rootObject;
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

        clazzParentsMap.put(rootObject.getClass(), Lists.newArrayList(rootObject));
    }


    /**
     * reflection based toString method
     *
     * @params ignore Entities and Collections (default = true), idOnly (default = false) -> only makes sense when first settings option is false,
     * only log LazyInitException (default=true)
     */
    public String toString() {
        if (parent == null) {
            return "null";
        }


        return (new ReflectionToStringBuilder(parent, ToStringStyle.SHORT_PREFIX_STYLE) {
            protected Object getValue(Field f) throws IllegalAccessException {

                // init propertyState
                property = new Property(f);
                property.entity = isEntity();
                property.collection = isCollection();
                property.ignored = ignoreEntitiesAndCollections;
                if (isWhiteListActive() && propertyWhiteList.contains(f.getName())) {
                    property.ignored = Boolean.FALSE;
                    property.whiteListed = Boolean.TRUE;
                }
                property.value = property.field.get(parent);

                String propertyString = "super";
                try {
                    if (property.isIgnored()) {
                        return "";
                    }
                    if (property.isEntity()) {
                        updateClazzParentsMap(property.value);
                        propertyString = entityToString();
                    } else if (property.isCollection()) {
                        updateClazzParentMapForCollection(property.value);
                        propertyString = collectionToString();
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (LazyInitializationException e) {
                    return lazyInitExceptionToString(e);
                }
                if (propertyString.equals("super")) {
                    return super.getValue(f);
                } else {
                    throw new RuntimeException("Unhandled Property" + property);
                }
            }
        }).toString();
    }

    protected void updateClazzParentMapForCollection(Object collection) throws IllegalAccessException {
        // check if collection empty, if so pick type of first entity found
        if (!((Collection) collection).isEmpty()) {
            for (Object entity : ((Collection<?>) collection)) {
                updateClazzParentsMap(entity);
            }
//            Object collectionEntity = ((Collection<?>) collection).stream().findFirst().get();
        }
    }

    protected void updateClazzParentsMap(Object child) throws IllegalAccessException {
        // dont use property.value here bc that would be the collection, we want the entity
//        Object child = property.field.get(entity);
        Class<?> parentClazz = property.field.getType();
        List<Object> objects = clazzParentsMap.get(parentClazz);
        if (objects == null) {
            clazzParentsMap.put(parentClazz, Lists.newArrayList(child));
        } else {
            objects.add(child);
        }
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
        IdentifiableEntity entity = ((IdentifiableEntity) property.field.get(parent));
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
        Collection<?> collection = (Collection<?>) property.field.get(parent);
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

    private String toId(IdentifiableEntity entity) {
        if (entity == null) {
            return "null";
        } else {
            return entity.getId() == null ? "null-id" : entity.getId().toString();
        }
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

    protected class Property {
        private Field field;
        private Boolean whiteListed = Boolean.FALSE;
        private Boolean entity = Boolean.FALSE;
        private Boolean collection = Boolean.FALSE;
        private Boolean ignored = Boolean.FALSE;
        private Boolean loaded = Boolean.FALSE;
        private Object value;

        public Property(Field field) {
            this.field = field;
        }

        public void addToWhiteList() {
            this.whiteListed = Boolean.TRUE;
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

        public void setLoaded() {
            PersistenceUnitUtil unitUtil =
                    entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

//                Assert.assertTrue(unitUtil.isLoaded(org));
//                // users is a field (Set of User) defined in Organization entity
//                Assert.assertFalse(unitUtil.isLoaded(org, "users"));
//
//                initializeCollection(org.getUsers());
//                Assert.assertTrue(unitUtil.isLoaded(org, "users"));
//                for(User user : org.getUsers()) {
//                    Assert.assertTrue(unitUtil.isLoaded(user));
//                    Assert.assertTrue(unitUtil.isLoaded(user.getOrganization()));
//                }
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