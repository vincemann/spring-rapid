package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

public interface RelationalEntityManager {

    public <E extends IdentifiableEntity> E save(E entity);
    public void remove(IdentifiableEntity entity) throws EntityNotFoundException, BadEntityException;
    public <E extends IdentifiableEntity> E update(E entity, Boolean full) throws EntityNotFoundException, BadEntityException;
}
