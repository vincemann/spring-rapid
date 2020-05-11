package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

public interface DtoPostProcessor<Dto,E extends IdentifiableEntity<?>> {
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass);
    public void postProcessDto(Dto dto, E entity) throws BadEntityException;
    public void postProcessEntity(E entity, Dto dto) throws BadEntityException, EntityNotFoundException;
}
