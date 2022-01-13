package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;

import java.io.Serializable;
import java.util.Optional;

public class EntityUtils {

    private static CrudServiceLocator crudServiceLocator;

    public EntityUtils(CrudServiceLocator crudServiceLocator) {
        EntityUtils.crudServiceLocator = crudServiceLocator;
    }

    public static <E extends IdentifiableEntity> E findEntity(E entity) throws EntityNotFoundException, BadEntityException {
        return findEntity(entity.getClass(),entity.getId());
    }

    public static <E extends IdentifiableEntity> E findEntity(Class clazz, Serializable id) throws EntityNotFoundException, BadEntityException {
        CrudService service = crudServiceLocator.find((Class<IdentifiableEntity>) clazz);
        if (service == null){
            throw new IllegalArgumentException("no service found for entity with id " + id);
        }
        if (id == null){
            throw new RuntimeException("id is null ");
        }
        Optional<IdentifiableEntity> byId = service.findById(id);
        if (byId.isEmpty()){
            throw new IllegalArgumentException("no entity found with id " + id + " of type: " + clazz.getSimpleName());
        }
        return (E) byId.get();
    }


}
