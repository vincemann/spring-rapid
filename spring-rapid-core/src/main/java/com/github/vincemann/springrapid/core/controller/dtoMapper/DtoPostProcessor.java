package com.github.vincemann.springrapid.core.controller.dtoMapper;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

public interface DtoPostProcessor<Dto,E extends IdentifiableEntity<?>> {
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass);
    public void postProcessDto(Dto dto, E entity) throws BadEntityException;
    public void postProcessEntity(E entity, Dto dto) throws BadEntityException, EntityNotFoundException;
}
