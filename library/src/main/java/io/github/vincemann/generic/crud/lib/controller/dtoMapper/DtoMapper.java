package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

/**
 * Maps a Dto to an Entity, or vice versa
 */
public interface DtoMapper {

    public boolean isDtoClassSupported(Class<? extends IdentifiableEntity> clazz);
    public <T extends IdentifiableEntity> T mapDtoToEntity(Object source, Class<T> destinationClass) throws EntityMappingException;
    public <T extends IdentifiableEntity> T mapEntityToDto(Object source, Class<T> destinationClass) throws EntityMappingException;
}
