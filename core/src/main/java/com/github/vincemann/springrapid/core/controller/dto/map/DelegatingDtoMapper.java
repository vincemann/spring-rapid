package com.github.vincemann.springrapid.core.controller.dto.map;

import com.github.vincemann.springrapid.core.controller.dto.DtoPostProcessor;
import com.github.vincemann.springrapid.core.controller.dto.EntityPostProcessor;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.util.List;
import java.util.Set;

public interface DelegatingDtoMapper {
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object dto, Class<T> destinationClass) throws EntityNotFoundException, BadEntityException;
    public <T> Set<T> mapToDto(Set<? extends IdentifiableEntity<?>> source, Class<T> destinationClass, String... fieldsToMap) throws BadEntityException;
    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass,String... fieldsToMap) throws BadEntityException;
    public <T> List<T> mapToDto(List<? extends IdentifiableEntity<?>> source, Class<T> destinationClass, String... fieldsToMap) throws BadEntityException;
    public void register(DtoMapper<?,?> dtoMapper);
    public void registerEntityPostProcessor(EntityPostProcessor postProcessor);

    public void registerDtoPostProcessor(DtoPostProcessor postProcessor);
}
