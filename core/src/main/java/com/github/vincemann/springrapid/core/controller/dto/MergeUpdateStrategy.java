package com.github.vincemann.springrapid.core.controller.dto;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;


public interface MergeUpdateStrategy {
    /**
     * Merges saved entity with patch entity.
     * Only merges fields from dtoClass.
     *
     * @return merged Entity
     */
    public <E extends IdentifiableEntity<?>> E merge(E patch, E saved, Class<?> dtoClass) throws BadEntityException;
}
