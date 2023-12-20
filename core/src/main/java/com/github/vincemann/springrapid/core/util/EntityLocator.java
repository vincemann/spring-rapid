package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
// dont make transactional - if code needs transaction, it should create it himself, then this call will also be wrapped in its own transaction
//@Transactional
public class EntityLocator {

    private static CrudServiceLocator crudServiceLocator;

    public EntityLocator(CrudServiceLocator crudServiceLocator) {
        EntityLocator.crudServiceLocator = crudServiceLocator;
    }

    public static <E extends IdentifiableEntity> Optional<E> findEntity(E entity) {
        return findEntity(entity.getClass(),entity.getId());
    }

    public static <E extends IdentifiableEntity> Optional<E> findEntity(Class clazz, Serializable id) {
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
