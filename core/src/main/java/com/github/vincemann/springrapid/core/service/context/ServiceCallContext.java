package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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


    private Map<EntityInformation,Optional<? extends IdentifiableEntity<?>>> entityCache = new HashMap<>();
    private Map<String,Optional<Object>> cache = new HashMap<>();

    @Setter
    private Class<?> currentEntityClass;


    @EqualsAndHashCode
    @Builder
    private static class EntityInformation{
        private Serializable id;
        private Class<?> entityClass;
    }


    public <T> T getCached(String key, Supplier<T> supplier){
        Optional<T> cached = (Optional<T>) cache.get(key);
        if (cached != null)
            return (T) cached;
        T value = supplier.get();
        cache.put(key,Optional.ofNullable(value));
        return value;
    }

    public void addCachedEntity(EntityInformation information, IdentifiableEntity<?> entity) {
        entityCache.put(information,Optional.ofNullable(entity));
    }

    public void addCachedEntity(EntityInformation information, Optional<? extends IdentifiableEntity<?>> entity) {
        entityCache.put(information,entity);
    }


    public <E extends IdentifiableEntity<?>> Optional<E> resolveEntity(Serializable id, Class<?> clazz) {
        EntityInformation info = new EntityInformation(id,clazz);
        Optional<E> cached = (Optional<E>) entityCache.get(info);
        if (cached == null){
            // not cached yet
            return forceResolveEntity(id, clazz);
//            entityCache.put(info,entity);
//            return entity;
        }else {
            // cached
            return cached;
        }
    }

    public <E extends IdentifiableEntity<?>> Optional<E> resolveEntity(Serializable id) {
        return resolveEntity(id,currentEntityClass);
    }


    /**
     * ignored caching and fetches entity from db.
     */
    public <E extends IdentifiableEntity<?>> Optional<E> forceResolveEntity(Serializable id, Class<?> entityClass) {
        if (id == null)
            throw new IllegalArgumentException("id is null, cannot resolve entity");
        Optional<E> entity = EntityLocator.findEntity(entityClass, id);
        entityCache.put(new EntityInformation(id,entityClass),entity);
        return entity;
//        return entity;
//        VerifyEntity.isPresent(entity,id,entityClass);
//        return (E) entity.get();
    }

}
