package com.github.vincemann.springrapid.core.controller.mergeUpdate;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

@LogInteraction
@LogException
public interface MergeUpdateStrategy extends AopLoggable {
    public <E extends IdentifiableEntity<?>> E merge(E patch, E saved, Class<?> dtoClass) throws BadEntityException;
}
