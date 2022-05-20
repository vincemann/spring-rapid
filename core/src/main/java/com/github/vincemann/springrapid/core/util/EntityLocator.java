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
@Transactional
public class EntityLocator {

    private CrudServiceLocator crudServiceLocator;


    public <E extends IdentifiableEntity> E findEntity(E entity) throws EntityNotFoundException, BadEntityException {
        return findEntity(entity.getClass(),entity.getId());
    }

    public <E extends IdentifiableEntity> E findEntity(Class clazz, Serializable id) throws EntityNotFoundException, BadEntityException {
        CrudService service = crudServiceLocator.find((Class<IdentifiableEntity>) clazz);
//        System.err.println("known services: " + crudServiceLocator.getEntityClassPrimaryServiceMap());
//        System.err.println(service);
        if (service == null){
            throw new IllegalArgumentException("no service found for entity with id " + id + " and clazz: " + clazz.getSimpleName());
        }
//        System.err.println(service.findAll());
        if (id == null){
            throw new RuntimeException("id is null ");
        }
        Optional<IdentifiableEntity> byId = service.findById(id);
        if (byId.isEmpty()){
            throw new EntityNotFoundException("no entity found with id " + id + " of type: " + clazz.getSimpleName());
        }
        return (E) byId.get();
    }

    @Autowired
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }
}
