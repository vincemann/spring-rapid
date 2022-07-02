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


    public static final String IGNORED_STRING = "<ignored>";
    public static final String IGNORED_UNLOADED_STRING = "<unloaded ignored>";
    public static final String TOO_MANY_ENTRIES_STRING = "<too many entries>";
    public static final String IGNORED_LOAD_BLACKLISTED_STRING = "<load blacklisted ignored>";
    public static final String LAZY_INIT_EXCEPTION_STRING = "<LazyInitializationException>";
    public static final String LAZY_INIT_EXCEPTION_LIST_STRING = "<[LazyInitializationException]>";


    // set in app config
    private static EntityManager entityManager;

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static void setEntityManager(EntityManager entityManager) {
        LazyLogger.entityManager = entityManager;
    }


    private Boolean ignoreLazyException = Boolean.TRUE;
    private Boolean ignoreEntities = Boolean.TRUE;
    private Boolean idOnly = Boolean.FALSE;
    private Integer maxEntitiesLoggedInCollections = null;
    private HashSet<String> propertyBlackList = new HashSet<>();
    private Map<String, Integer> maxEntitiesLoggedPropertyMap = new HashMap<>();

    private Boolean onlyLogLoaded = Boolean.TRUE;
    private Set<String> logLoadedBlacklist = new HashSet<>();

    private Property property;
    private Map<Thread,Map<Object,String>> alreadySeenThreadMap = new HashMap<>();
//    private Map<Class, List<Object>> clazzParentsMap = new HashMap<>();


    @Builder
    public LazyLogger(Boolean ignoreEntities, Boolean idOnly, Boolean ignoreLazyException, HashSet<String> propertyBlackList, Integer maxEntitiesLoggedInCollections, Boolean onlyLogLoaded, Set<String> logLoadedBlacklist, Map<String, Integer> maxEntitiesLoggedPropertyMap) {
        if (ignoreEntities != null)
            this.ignoreEntities = ignoreEntities;
        if (idOnly != null)
            this.idOnly = idOnly;
        if (ignoreLazyException != null)
            this.ignoreLazyException = ignoreLazyException;
        if (propertyBlackList != null)
            this.propertyBlackList = propertyBlackList;
        if (maxEntitiesLoggedInCollections != null)
            this.maxEntitiesLoggedInCollections = maxEntitiesLoggedInCollections;
        if (onlyLogLoaded != null)
            this.onlyLogLoaded = onlyLogLoaded;
        if (logLoadedBlacklist != null)
            this.logLoadedBlacklist = logLoadedBlacklist;
        if (maxEntitiesLoggedPropertyMap != null)
            this.maxEntitiesLoggedPropertyMap = maxEntitiesLoggedPropertyMap;
    }

//    protected void initParent(Object parent) {
//        clazzParentsMap.put(parent.getClass(), Lists.newArrayList(parent));
//    }

    /**
     * reflection based toString method
     *
     * @params ignore Entities and Collections (default = true), idOnly (default = false) -> only makes sense when first settings option is false,
     * only log LazyInitException (default=true)
     */
    public String toString(Object parent) {
        if (parent == null) {
            return "null";
        }

        // prohibit endless backref loops
        Map<Object,String> alreadySeenMap = new HashMap<>();
        this.alreadySeenThreadMap.put(Thread.currentThread(),alreadySeenMap);


        String result = (new ReflectionToStringBuilder(parent, ToStringStyle.SHORT_PREFIX_STYLE) {
            protected Object getValue(Field f) throws IllegalAccessException {
                System.err.println(" checking field: " + f.getName().toUpperCase());

                // init propertyState
                property = new Property(f);
                property.entity = isEntity();
                property.collection = isCollection();
                property.parent = parent;
                // dont do this bc this will load lazy property
//                property.value = property.field.get(parent);

                String propertyString = "super";
                try {
                    if (isIgnored()) {
                        log.debug("result of field: " + f.getName().toUpperCase() + " : found property string super value: " + " ignored");
                        return remember(IGNORED_STRING);
                    }
                    if (property.isEntity()) {
//                        updateClazzParentsMap(property.value);
                        propertyString = loadIfWanted(parent, property.field.getName(), Boolean.FALSE);
                    } else if (property.isCollection()) {
//                        updateClazzParentMapForCollection(property.value);
                        propertyString = loadIfWanted(parent, property.field.getName(), Boolean.TRUE);
                    }


                    if (propertyString.equals("super")) {
                        Object superValue = super.getValue(f);
                        log.debug("result of field: " + f.getName().toUpperCase() + " : found property string super value: " + superValue);
                        return superValue;
                    } else {
                        log.debug("result of field: " + f.getName().toUpperCase() + " : found property string own value: " + propertyString);
                        return remember(propertyString);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (LazyInitializationException e) {
                    propertyString = lazyInitExceptionToString(e);
                    log.debug("result of field: " + f.getName().toUpperCase() + " : found property string own value: " + propertyString);
                    return remember(propertyString);
                }
            }
        }).toString();
        alreadySeenThreadMap.remove(Thread.currentThread());
        return result;
    }

    protected Boolean isIgnored() {
        Boolean ignored = ignoreEntities;
        if (isPropertyBlackListActive() && propertyBlackList.contains(property.field.getName())) {
            ignored = Boolean.TRUE;
            property.blackListed = Boolean.TRUE;
        }
        // todo work on isCollection -> non Entity Collections should get logged
        if (!isEntity() && !isCollection()) {
            ignored = Boolean.FALSE;
        }
        return ignored;
    }

    protected String loadIfWanted(Object parent, String propertyName, Boolean collection) throws IllegalAccessException {
        String propertyString = "super";
        if (checkIfLoaded(parent, propertyName)) {
            // not blacklisted
            if (onlyLogLoaded) {
                if (logLoadedBlacklist.contains(propertyName)) {
                    // dont load bc blacklisted
                    return IGNORED_LOAD_BLACKLISTED_STRING;
                }
            }
            loadPropertyValue();
            propertyString = entitiesToString(collection);
        } else {
            // not loaded
            if (onlyLogLoaded) {
                // whiteList does not really make sense in this context
//                if (logLoadedWhitelist.contains(propertyName)) {
//                    // still load it bc its whiteListed, load collection if necessary
//                    loadUnloadedValue();
//                    propertyString = entitiesToString(collection);
//                } else {
                return IGNORED_UNLOADED_STRING;
//                }
            }
        }
        return propertyString;
    }

    protected String entitiesToString(Boolean collection) throws IllegalAccessException {
        if (collection) {
            return collectionToString();
        } else {
            return entityToString();
        }
    }

    protected void loadPropertyValue() throws IllegalAccessException {
        // now its safe to load the value aka is already loaded
        property.value = property.field.get(property.parent);
    }

    protected boolean checkIfLoaded(Object parent, String childPropertyName) {
//        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
//            // either loaded or not and will crash with lazyInitException
//            return true;
//        }
        PersistenceUnitUtil persistenceUtil =
                entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        boolean loaded = persistenceUtil.isLoaded(parent, childPropertyName);
        log.debug("property " + childPropertyName + " loaded? : " + loaded);
        return loaded;
    }

//    protected void updateClazzParentMapForCollection(Object collection) throws IllegalAccessException {
//        // check if collection empty, if so pick type of first entity found
//        if (!((Collection) collection).isEmpty()) {
//            for (Object entity : ((Collection<?>) collection)) {
//                updateClazzParentsMap(entity);
//            }
////            Object collectionEntity = ((Collection<?>) collection).stream().findFirst().get();
//        }
//    }
//
//    protected void updateClazzParentsMap(Object child) throws IllegalAccessException {
//        // dont use property.value here bc that would be the collection, we want the entity
////        Object child = property.field.get(entity);
//        Class<?> parentClazz = property.field.getType();
//        List<Object> objects = clazzParentsMap.get(parentClazz);
//        if (objects == null) {
//            clazzParentsMap.put(parentClazz, Lists.newArrayList(child));
//        } else {
//            objects.add(child);
//        }
//    }

    protected String lazyInitExceptionToString(LazyInitializationException e) {
        log.trace(e.getMessage());
        if (property.isEntity()) {
            log.warn("Could not log jpa lazy entity field: " + property.field.getName() + ", skipping.");
            if (ignoreLazyException) {
                return LAZY_INIT_EXCEPTION_STRING;
            } else {
                throw e;
            }
        } else if (property.isCollection()) {
            log.warn("Could not log jpa lazy collection field: " + property.field.getName() + ", skipping.");
//                        log.warn("Use @LogInteractions transactional flag to load all lazy collections for logging");
            if (ignoreLazyException) {
                return LAZY_INIT_EXCEPTION_LIST_STRING;
            } else {
                throw e;
            }
        } else {
            // this should never happen
            throw new RuntimeException("LazyInitException without entities being detected");
        }
    }

    protected Boolean isPropertyBlackListActive() {
        return !propertyBlackList.isEmpty();
    }

    protected Boolean isEntity() {
        return IdentifiableEntity.class.isAssignableFrom(property.field.getType());
    }

    protected Boolean isCollection() {
        return Collection.class.isAssignableFrom(property.field.getType());
    }

    protected String entityToString() throws IllegalAccessException {
        IdentifiableEntity entity = (IdentifiableEntity) property.value;
        if (entity == null) {
            return "null";
        }
        if (idOnly) {
            return toId(entity);
        }
        return "super";
    }

    protected String collectionToString() throws IllegalAccessException {
        Collection<?> collection = (Collection<?>) property.value;
        if (collection == null) {
            return "null";
        }
        // test for lazy init exception already with size call
        if (collection.size() > 0) {
            if (hasTooManyEntries(collection)){
                return TOO_MANY_ENTRIES_STRING;
            }
            if (idOnly) {
                String s = collectionToIdString(collection);
                if (!s.equals("super")) {
                    return s;
                }
            }
        }
        return "super";
    }

    private Boolean hasTooManyEntries(Collection collection){
        Integer maxEntities = maxEntitiesLoggedPropertyMap.get(property.field.getName());
        if (maxEntities != null) {
            if (collection.size() > maxEntities) {
                return Boolean.TRUE;
            }
        }

        if (maxEntitiesLoggedInCollections != null && collection.size() > maxEntitiesLoggedInCollections) {
            // todo implement api for logging only maxEntitiesLoggedInCollection entities and not just stop
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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

    protected String remember(String s){
        Map<Object, String> alreadySeenMap = alreadySeenThreadMap.get(Thread.currentThread());
        String put = alreadySeenMap.put(property.value, s);
        if (put != null){
            throw new IllegalArgumentException("returned already seen value");
        }
        return s;
    }

    // todo change design, put everything in here or dont use such a class
    protected static class Property {
        private Object parent;
        private Field field;
        private Boolean blackListed = Boolean.FALSE;
        private Boolean entity = Boolean.FALSE;
        private Boolean collection = Boolean.FALSE;
        private Boolean ignored = Boolean.FALSE;
        private Boolean loaded = Boolean.FALSE;
        private Object value;

        public Property(Field field) {
            this.field = field;
        }

        public Boolean isEntity() {
            return entity;
        }

        public Boolean isCollection() {
            return collection;
        }

//        public Boolean isLoaded() {
//            return loaded;
//        }

//        public void setLoaded() {
//            PersistenceUnitUtil unitUtil =
//                    entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
//
////                Assert.assertTrue(unitUtil.isLoaded(org));
////                // users is a field (Set of User) defined in Organization entity
////                Assert.assertFalse(unitUtil.isLoaded(org, "users"));
////
////                initializeCollection(org.getUsers());
////                Assert.assertTrue(unitUtil.isLoaded(org, "users"));
////                for(User user : org.getUsers()) {
////                    Assert.assertTrue(unitUtil.isLoaded(user));
////                    Assert.assertTrue(unitUtil.isLoaded(user.getOrganization()));
////                }
//        }


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