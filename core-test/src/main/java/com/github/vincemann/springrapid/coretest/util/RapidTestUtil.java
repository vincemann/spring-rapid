package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

public class RapidTestUtil {

    public static <E extends IdentifiableEntity> E mustBePresentIn(CrudRepository repo, Serializable id){
        Optional byId = repo.findById(id);
        if (byId.isEmpty()){
            throw new IllegalArgumentException("No Entity found with id: " + id);
        }
        return (E) byId.get();
    }

    public static <E extends IdentifiableEntity> E mustBePresentIn(CrudService service, Serializable id){
        Optional byId = null;
        try {
            byId = service.findById(id);
        } catch (BadEntityException e) {
            throw new RuntimeException(e);
        }
        if (byId.isEmpty()){
            throw new IllegalArgumentException("No Entity found with id: " + id);
        }
        return (E) byId.get();
    }

    public static void clear(CrudService crudService){
        for (IdentifiableEntity entity : (Set<IdentifiableEntity>) crudService.findAll()) {
            try {
                crudService.deleteById(entity.getId());
            } catch (EntityNotFoundException|BadEntityException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
