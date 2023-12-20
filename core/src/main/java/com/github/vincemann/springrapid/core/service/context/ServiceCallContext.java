package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class ServiceCallContext {


    private Map<EntityInformation,IdentifiableEntity<?>> cache = new HashMap<>();

    @Setter
    private Class<?> currentEntityClass;


    @EqualsAndHashCode
    @Builder
    private static class EntityInformation{
        private Serializable id;
        private Class<?> entityClass;
    }



    public void addCachedEntity(EntityInformation information, IdentifiableEntity<?> entity) {
        cache.put(information,entity);
    }


    public <E extends IdentifiableEntity<?>> E resolveEntity(Serializable id, Class<?> clazz) throws EntityNotFoundException {
        EntityInformation info = new EntityInformation(id,clazz);
        IdentifiableEntity<?> cached = cache.get(info);
        if (cached == null){
            // not cached yet
            IdentifiableEntity<?> entity = forceResolveEntity(id, clazz);
            cache.put(info,entity);
            return (E) entity;
        }else {
            // cached
            return (E) cached;
        }
    }

    public <E extends IdentifiableEntity<?>> E resolveEntity(Serializable id) throws EntityNotFoundException {
        return resolveEntity(id,currentEntityClass);
    }



    public <E extends IdentifiableEntity<?>> E forceResolveEntity(Serializable id, Class<?> entityClass) throws EntityNotFoundException {
        if (id == null)
            throw new IllegalArgumentException("id is null, cannot resolve entity");
        Optional<IdentifiableEntity> entity = EntityLocator.findEntity(entityClass, id);
        VerifyEntity.isPresent(entity,id,entityClass);
        return (E) entity.get();
    }

}
