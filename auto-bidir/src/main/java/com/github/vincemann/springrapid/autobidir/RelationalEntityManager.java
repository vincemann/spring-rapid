package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

public interface RelationalEntityManager {

    public <E extends IdentifiableEntity> E save(E entity, String... membersToCheck);
    public void remove(IdentifiableEntity entity, String... membersToCheck) throws EntityNotFoundException, BadEntityException;
    <E extends IdentifiableEntity> E partialUpdate(E managed, E oldEntity, E partialUpdateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException;
    public <E extends IdentifiableEntity> E update(E managed, E oldEntity, E updateEntity, String... membersToCheck) throws EntityNotFoundException, BadEntityException;

}
