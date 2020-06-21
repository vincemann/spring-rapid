package com.github.vincemann.springrapid.core.controller.mergeUpdate;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface MergeUpdateStrategy {
    public <E extends IdentifiableEntity<?>> E merge(E patch, E saved, Class<?> dtoClass) throws BadEntityException;
}
