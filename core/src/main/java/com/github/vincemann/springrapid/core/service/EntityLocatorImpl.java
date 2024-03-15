package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.HibernateProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Optional;

public class EntityLocatorImpl implements EntityLocator {
    private CrudServiceLocator crudServiceLocator;


    public <E extends IdentifiableEntity> Optional<E> findEntity(E entity) {
        return findEntity(HibernateProxyUtils.getTargetClass(entity),entity.getId());
    }


    public <E extends IdentifiableEntity> Optional<E> findEntity(Class clazz, Serializable id) {
        Assert.notNull(id,"id must not be null");
        CrudService service = crudServiceLocator.find((Class<IdentifiableEntity>) clazz);
        Assert.notNull(service,"no service found for entity clazz: " + clazz.getSimpleName());
        return service.findById(id);
    }

    @Autowired
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }
}
