package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.aoplog.api.LogConfig;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;


//override config from CrudService -> explicitly enable Logging for methods that actually matter
@LogInteraction(disabled = true)
//@LogConfig
public interface CrudServiceExtension<S extends CrudService>
            extends CrudService, NextLinkAware<S>{



    @Override
    default Optional findById(Serializable id) throws BadEntityException {
        return getNext().findById(id);
    }

    @Override
    default IdentifiableEntity update(IdentifiableEntity entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }

    @Override
    default IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        return getNext().save(entity);
    }

    @Override
    default void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
        getNext().deleteById(id);
    }

    @Override
    public default Set<IdentifiableEntity> findAll() {
        return getNext().findAll();
    }

    // todo change to getLast()
    @Override
    public default Class<IdentifiableEntity> getEntityClass() {
        return getNext().getEntityClass();
    }


    //    @Override
//    public default CrudRepository getRepository() {
//        return getNext().getRepository();
//    }

}
