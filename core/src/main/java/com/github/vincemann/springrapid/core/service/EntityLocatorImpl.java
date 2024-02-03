package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
public class EntityLocatorImpl implements EntityLocator {
    private CrudServiceLocator crudServiceLocator;

    @Autowired
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    public <E extends IdentifiableEntity> Optional<E> findEntity(E entity) {
        return findEntity(ProxyUtils.getTargetClass(entity),entity.getId());
    }


    public <E extends IdentifiableEntity> Optional<E> findEntity(Class clazz, Serializable id) {
        CrudService service = crudServiceLocator.find((Class<IdentifiableEntity>) clazz);
//        System.err.println("known services: " + crudServiceLocator.getEntityClassPrimaryServiceMap());
//        System.err.println(service);
        if (service == null){
            throw new IllegalArgumentException("no service found for entity with id " + id + " and clazz: " + clazz.getSimpleName());
        }
//        System.err.println(service.findAll());
        if (id == null){
            throw new IllegalArgumentException("id is null ");
        }
        return service.findById(id);
    }
}
