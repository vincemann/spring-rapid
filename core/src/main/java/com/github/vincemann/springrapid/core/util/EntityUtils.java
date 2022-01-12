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

    public static <E extends IdentifiableEntity> E findOldEntity(E entity) throws EntityNotFoundException, BadEntityException {
        Class entityClass = entity.getClass();
        CrudService service = crudServiceLocator.find((Class<IdentifiableEntity>) entityClass);
        if (service == null){
            throw new IllegalArgumentException("no service found for entity: " + entity);
        }
        Serializable id = entity.getId();
        if (id == null){
            throw new RuntimeException("id is null of entity: " + entity);
        }
        Optional<IdentifiableEntity> byId = service.findById(id);
        if (byId.isEmpty()){
            throw new RuntimeException("no entity found for entity: " + entity);
        }
        return (E) byId.get();
    }


}
