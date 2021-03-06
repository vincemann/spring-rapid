package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

@LogInteraction
//@LogException
public interface DtoEntityPostProcessor<Dto,E/* extends IdentifiableEntity<?>*/> extends AopLoggable {

    @LogInteraction(disabled = true)
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass);
    public void postProcessEntity(E entity, Dto dto) throws BadEntityException, EntityNotFoundException;
}
