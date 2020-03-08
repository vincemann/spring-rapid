package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

/**
 * Maps a Dto to an Entity, or vice versa
 */
public interface DtoMapper {

    public boolean isDtoClassSupported(Class<? extends IdentifiableEntity> clazz);
    public <T extends IdentifiableEntity> T mapToEntity(IdentifiableEntity source,Class<T> destinationClass) throws DtoMappingException;
    public <T extends IdentifiableEntity> T mapToDto(IdentifiableEntity source, Class<T> destinationClass) throws DtoMappingException;
}
