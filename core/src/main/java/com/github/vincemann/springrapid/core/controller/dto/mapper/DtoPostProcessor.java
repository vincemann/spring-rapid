package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface DtoPostProcessor<Dto,E/* extends IdentifiableEntity<?>*/> {
    @LogInteraction(disabled = true)
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass);
    public void postProcessDto(Dto dto, E entity, String... fieldsToMap) throws BadEntityException;
}
