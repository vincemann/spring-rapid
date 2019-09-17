package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;

/**
 * maps a Dto Entity to a ServiceEntity, or vice versa
 */
public interface DtoMapper {
    public <T extends IdentifiableEntity> T mapDtoToServiceEntity(Object source, Class<T> destinationClass) throws EntityMappingException;
    public <T extends IdentifiableEntity> T mapServiceEntityToDto(Object source, Class<T> destinationClass) throws EntityMappingException;
}
