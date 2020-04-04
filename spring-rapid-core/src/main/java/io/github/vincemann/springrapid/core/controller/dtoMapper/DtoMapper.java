package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;

/**
 * Maps a Dto to an Entity, or vice versa
 */
public interface DtoMapper {

    public boolean isDtoClassSupported(Class<? extends IdentifiableEntity> clazz);
    public <T extends IdentifiableEntity> T mapToEntity(IdentifiableEntity source,Class<T> destinationClass) throws DtoMappingException;
    public <T extends IdentifiableEntity> T mapToDto(IdentifiableEntity source, Class<T> destinationClass) throws DtoMappingException;
}
