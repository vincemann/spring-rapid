package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@NoArgsConstructor
public abstract class AbstractServiceCallContext {

    protected Map<String,Object> cache = new HashMap<>();
    protected Map<String,Object> values = new HashMap<>();

    protected Map<String,Boolean> cacheDirtyMap = new HashMap<>();

    protected EntityLocator entityLocator;

//    @Setter
//    private Class<?> currentEntityClass;

    @Autowired
    public void setEntityLocator(EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }


    //    @Setter
//    private Class<?> currentEntityClass;

    public void setCacheDirty(String key, Boolean cacheDirty) {
        cacheDirtyMap.put(key,cacheDirty);
    }

    public Object removeCachedEntity(Class entityClass, Serializable id){
        return cache.remove(computeKey(entityClass,id));
    }


    public <T> T getCached(String key, Supplier<T> supplier){
        if (!isCacheDirty(key)){
            Optional<T> cached = (Optional<T>) cache.get(key);
            if (cached != null){
                if (cached.isPresent())
                    return cached.get();
                else
                    return null;
            }
        }

        T value = supplier.get();
        setCachedEntity(key,value);
        return value;
    }

    public <T> Optional<T> getCachedEntity(Class entityClass, Serializable id){
        String key = computeKey(entityClass, id);
        if (!isCacheDirty(key)){
            Optional<T> cached = (Optional<T>) cache.get(key);
            if (cached != null){
                return cached;
            }
        }
        return null;
    }

    protected void setCachedEntity(String key, Object entity){
        cache.put(key,Optional.ofNullable(entity));
        setCacheDirty(key,Boolean.FALSE);
    }

    protected void setCachedEntity(IdentifiableEntity<?> entity){
        String key = computeKey(entity.getClass(), entity.getId());
        setCachedEntityOptional(key,Optional.of(entity));
    }

    protected void setCachedEntityOptional(String key, Optional<? extends IdentifiableEntity<?>> entity){
        cache.put(key,entity);
        setCacheDirty(key,Boolean.FALSE);
    }

    protected boolean isCacheDirty(String key){
        return cacheDirtyMap.getOrDefault(key,Boolean.FALSE);
    }

    public <T,E extends Exception> T getThrowingCached(String key, ThrowingSupplier<T,E> supplier) throws E {
        if (!isCacheDirty(key)){
            Optional<T> cached = (Optional<T>) cache.get(key);
            if (cached != null){
                if (cached.isPresent())
                    return cached.get();
                else
                    return null;
            }
        }

        T value = supplier.get();
        setCachedEntity(key,value);
        return value;
    }

    public <T> T getCached(Serializable id, Class clazz, Supplier<T> supplier){
        return getCached(computeKey(clazz,id),supplier);
    }

//    public <T> T getCached(Serializable id, Supplier<T> supplier){
//        return getCached(computeKey(currentEntityClass,id),supplier);
//    }

    public <T,E extends Exception> T getThrowingCached(Serializable id, Class clazz, ThrowingSupplier<T,E> supplier) throws E {
        return getThrowingCached(computeKey(clazz,id),supplier);
    }

//    public <T,E extends Exception> T getThrowingCached(Serializable id, ThrowingSupplier<T,E> supplier) throws E {
//        return getThrowingCached(computeKey(currentEntityClass,id),supplier);
//    }


    public <T> T getRefreshedCached(String key, Supplier<T> supplier){
        T value = supplier.get();
        setCachedEntity(key,value);
        return value;
    }

    public void addCached(String key, Object value) {
        cache.put(key,value);
    }

    public void setValue(String key, Object value) {
        values.put(key,value);
    }

    public <T> T getValue(String key) {
        return (T) values.get(key);
    }

    public void clearValue(String key) {
        values.remove(key);
    }


    public <T> T getValueOrDefault(String key, T defaultValue) {
        return (T) values.getOrDefault(key,defaultValue);
    }

//    public void addCachedEntity(IdentifiableEntity<?> entity) {
//        addCachedEntity(computeKey(entity.getClass(),entity.getId()),entity);
//    }

    public void setCachedEntity(Class clazz, Serializable id, Optional<? extends IdentifiableEntity<?>> entity) {
        setCachedEntityOptional(computeKey(clazz,id),entity);
    }

    // entity is expected to be found, otherwise EntityNotFoundException is thrown
    public <E extends IdentifiableEntity<?>> E resolvePresentEntity(Serializable id, Class<?> clazz) throws EntityNotFoundException {
        Optional<E> entity = resolveEntity(id, clazz);
        if (entity.isEmpty())
            throw new EntityNotFoundException(id,clazz);
        else
            return entity.get();
    }

//    public <E extends IdentifiableEntity<?>> E resolvePresentEntity(Serializable id) throws EntityNotFoundException {
//       return resolvePresentEntity(id,currentEntityClass);
//    }


    public <E extends IdentifiableEntity<?>> Optional<E> resolveEntity(Serializable id, Class<?> clazz) {
        if (id == null)
            throw new IllegalArgumentException("id is null, cannot resolve entity");
        Supplier<Optional<E>> supplier = () -> entityLocator.findEntity(clazz,id);
        String key = computeKey(clazz, id);
        return getCached(key,supplier);
    }

    public static String computeKey(Class clazz, Serializable id){
        return clazz.toString() + ":" + String.valueOf(id);
    }


//    public <E extends IdentifiableEntity<?>> Optional<E> resolveEntity(Serializable id) {
//        return resolveEntity(id,currentEntityClass);
//    }


    /**
     * ignores caching and fetches entity from db.
     */
    public <E extends IdentifiableEntity<?>> Optional<E> resolveRefreshedEntity(Serializable id, Class<?> entityClass) {
        return getRefreshedCached(computeKey(entityClass,id),() -> entityLocator.findEntity(entityClass,id));
    }

    public <E extends IdentifiableEntity<?>> E resolvePresentRefreshedEntity(Serializable id, Class<?> entityClass) throws EntityNotFoundException {
        Optional<E> entity = resolveRefreshedEntity(id, entityClass);
        if (entity.isEmpty())
            throw new EntityNotFoundException(id,entityClass);
        else
            return entity.get();
    }

//    public <E extends IdentifiableEntity<?>> E resolvePresentRefreshedEntity(Serializable id) throws EntityNotFoundException {
//       return resolvePresentRefreshedEntity(id,currentEntityClass);
//    }
}
