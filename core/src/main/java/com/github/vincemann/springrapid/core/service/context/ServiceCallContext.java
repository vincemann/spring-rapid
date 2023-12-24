package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Gives user methods for costly operations whos result will be cached within one service call (across extensions).
 * Also provides convenience method for findById fetching operations: {@link this#resolveEntity(Serializable)}.
 * -> Provides separate cache for that.
 * Uses {@link com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator} for that under the hood.
 */
@NoArgsConstructor
public class ServiceCallContext {

    private Map<String,Object> cache = new HashMap<>();
    private Map<String,Object> values = new HashMap<>();

    private Map<String,Boolean> cacheDirtyMap = new HashMap<>();

    @Setter
    private Class<?> currentEntityClass;

    public void setCacheDirty(String key, Boolean cacheDirty) {
        cacheDirtyMap.put(key,cacheDirty);
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
        addCachedEntity(key,value);
        return value;
    }

    private void addCachedEntity(String key, Object entity){
        cache.put(key,Optional.ofNullable(entity));
        setCacheDirty(key,Boolean.FALSE);
    }

    private boolean isCacheDirty(String key){
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
        addCachedEntity(key,value);
        return value;
    }

    public <T> T getCached(Serializable id, Class clazz, Supplier<T> supplier){
       return getCached(computeKey(clazz,id),supplier);
    }

    public <T> T getCached(Serializable id, Supplier<T> supplier){
        return getCached(computeKey(currentEntityClass,id),supplier);
    }

    public <T,E extends Exception> T getThrowingCached(Serializable id, Class clazz, ThrowingSupplier<T,E> supplier) throws E {
        return getThrowingCached(computeKey(clazz,id),supplier);
    }

    public <T,E extends Exception> T getThrowingCached(Serializable id, ThrowingSupplier<T,E> supplier) throws E {
        return getThrowingCached(computeKey(currentEntityClass,id),supplier);
    }


    public <T> T getRefreshedCached(String key, Supplier<T> supplier){
        T value = supplier.get();
        addCachedEntity(key,value);
        return value;
    }

    public void addCached(String key, Object value) {
        cache.put(key,value);
    }

    public void addValue(String key, Object value) {
        values.put(key,value);
    }

    public <T> T getValue(String key) {
        return (T) values.get(key);
    }

    public <T> T getValueOrDefault(String key, T defaultValue) {
        return (T) values.getOrDefault(key,defaultValue);
    }

    public void addCachedEntity(IdentifiableEntity<?> entity) {
        addCachedEntity(computeKey(entity.getClass(),entity.getId()),entity);
    }

    public void addCachedEntity(Class clazz, Serializable id, Optional<? extends IdentifiableEntity<?>> entity) {
        addCachedEntity(computeKey(clazz,id),entity);
    }

    // entity is expected to be found, otherwise EntityNotFoundException is thrown
    public <E extends IdentifiableEntity<?>> E resolvePresentEntity(Serializable id, Class<?> clazz) throws EntityNotFoundException {
        Optional<E> entity = resolveEntity(id, clazz);
        if (entity.isEmpty())
            throw new EntityNotFoundException(id,clazz);
        else
            return entity.get();
    }

    public <E extends IdentifiableEntity<?>> E resolvePresentEntity(Serializable id) throws EntityNotFoundException {
       return resolvePresentEntity(id,currentEntityClass);
    }


    public <E extends IdentifiableEntity<?>> Optional<E> resolveEntity(Serializable id, Class<?> clazz) {
        if (id == null)
            throw new IllegalArgumentException("id is null, cannot resolve entity");
        Supplier<Optional<E>> supplier = () -> EntityLocator.findEntity(clazz,id);
        String key = computeKey(clazz, id);
        return getCached(key,supplier);
    }

    private static String computeKey(Class clazz, Serializable id){
        return clazz.toString() + ":" + String.valueOf(id);
    }


    public <E extends IdentifiableEntity<?>> Optional<E> resolveEntity(Serializable id) {
        return resolveEntity(id,currentEntityClass);
    }


    /**
     * ignores caching and fetches entity from db.
     */
    public <E extends IdentifiableEntity<?>> Optional<E> resolveRefreshedEntity(Serializable id, Class<?> entityClass) {
        return getRefreshedCached(computeKey(entityClass,id),() -> EntityLocator.findEntity(entityClass,id));
    }

    public <E extends IdentifiableEntity<?>> E resolvePresentRefreshedEntity(Serializable id, Class<?> entityClass) throws EntityNotFoundException {
        Optional<E> entity = resolveRefreshedEntity(id, entityClass);
        if (entity.isEmpty())
            throw new EntityNotFoundException(id,entityClass);
        else
            return entity.get();
    }

    public <E extends IdentifiableEntity<?>> E resolvePresentRefreshedEntity(Serializable id) throws EntityNotFoundException {
       return resolvePresentRefreshedEntity(id,currentEntityClass);
    }

}
