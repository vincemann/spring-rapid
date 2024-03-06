package com.github.vincemann.springrapid.core.controller.dto;


import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

public interface EntityPostProcessor<Dto,E> {

    public boolean supports(Class<?> entityClazz, Class<?> dtoClass);
    public void postProcessEntity(E entity, Dto dto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException;
}
