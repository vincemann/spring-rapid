package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Optional;

@NoArgsConstructor
public class ServiceCallContext {
    private static CrudServiceLocator crudServiceLocator;

    public static void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        ServiceCallContext.crudServiceLocator = crudServiceLocator;
    }

    @Getter
    private Class<? extends IdentifiableEntity<?>> entityClass;
    private Serializable id;

    public void setEntityClass(Class<? extends IdentifiableEntity<?>> entityClass) {
        this.entityClass = entityClass;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public ServiceCallContext(Class<IdentifiableEntity<?>> entityClass, Serializable id){
        this.entityClass = entityClass;
        this.id = id;
    }

    public ServiceCallContext(IdentifiableEntity<?> entity){
        this.entityClass = (Class<? extends IdentifiableEntity<?>>) entity.getClass();
        this.id = entity.getId();
    }




    public void setCachedEntity(IdentifiableEntity<?> entity){
        this.resolved = Boolean.TRUE;
        this.cachedEntity = entity;
    }


    private IdentifiableEntity<?> cachedEntity;
    private Boolean resolved = Boolean.FALSE;


    public <E extends IdentifiableEntity<?>> E resolveEntity(){
        if (id == null)
            throw new IllegalArgumentException("id is null, cannot resolve entity");
        if (!resolved){
            cachedEntity = forceResolveEntity();
            resolved = Boolean.TRUE;
        }
        return (E) cachedEntity;
    }


//    public <E extends IdentifiableEntity<?>> Optional<E> fetchEntity(){
//        return crudServiceLocator.find(entityClass).findById(id);
//    }

    public <E extends IdentifiableEntity<?>> E forceResolveEntity(){
        if (id == null)
            throw new IllegalArgumentException("id is null, cannot resolve entity");
        try {
            return EntityLocator.findEntity(entityClass,id);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
