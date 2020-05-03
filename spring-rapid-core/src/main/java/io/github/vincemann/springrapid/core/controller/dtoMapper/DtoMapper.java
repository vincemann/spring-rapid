package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

/**
 * Maps a Dto to an Entity, or vice versa
 */
public interface DtoMapper {

    public boolean isDtoClassSupported(Class<?> clazz);
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object source,Class<T> destinationClass) throws DtoMappingException;
    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass) throws DtoMappingException;
}
