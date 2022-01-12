package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

public interface RelationalEntityManager {

    public <E extends IdentifiableEntity> E save(E entity);
    public void remove(IdentifiableEntity entity) throws EntityNotFoundException, BadEntityException;

    <E extends IdentifiableEntity> E partialUpdate(E oldEntity, E updateEntity, E partialUpdateEntity) throws EntityNotFoundException, BadEntityException;

    public <E extends IdentifiableEntity> E update(E oldEntity, E updateEntity) throws EntityNotFoundException, BadEntityException;

}
