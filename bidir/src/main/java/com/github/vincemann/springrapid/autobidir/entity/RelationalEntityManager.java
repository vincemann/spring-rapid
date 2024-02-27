package com.github.vincemann.springrapid.autobidir.entity;

import com.github.vincemann.springrapid.autobidir.RelationalEntityAdvice;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

/**
 * Core component of bidir package.
 * Offers methods for managing bidir relationships for crud operations - call these before the respective service call
 * in order to set backrefs.
 *
 * Usually its enough to use annotation based approach via {@link com.github.vincemann.springrapid.autobidir.EnableAutoBiDir} and {@link com.github.vincemann.springrapid.autobidir.DisableAutoBiDir},
 * which are handled via aop by {@link RelationalEntityAdvice}, which internally calls this components method.
 */
public interface RelationalEntityManager {

    public <E extends IdentifiableEntity> E create(E entity, String... membersToCheck);
    public void delete(IdentifiableEntity entity, String... membersToCheck) throws EntityNotFoundException, BadEntityException;
    <E extends IdentifiableEntity> E partialUpdate(E managed, E oldEntity, E partialUpdateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException;
    public <E extends IdentifiableEntity> E fullUpdate(E managed, E oldEntity, E updateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException;

}
