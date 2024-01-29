package com.github.vincemann.springrapid.core.controller.dto;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;


@LogInteraction
//@LogException
public interface MergeUpdateStrategy extends AopLoggable {
    /**
     * Merges saved entity with patch entity.
     * Only merges fields from dtoClass.
     *
     * @return merged Entity
     * @throws BadEntityException
     */
    public <E extends IdentifiableEntity<?>> E merge(E patch, E saved, Class<?> dtoClass) throws BadEntityException;
}
