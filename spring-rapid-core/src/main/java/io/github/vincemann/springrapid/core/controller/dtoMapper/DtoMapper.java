package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

/**
 * Maps a Dto to an Entity, or vice versa
 */
public interface DtoMapper<E extends IdentifiableEntity<?>,Dto> {

    public boolean supports(Class<?> dtoClass);
    public <T extends E> T mapToEntity(Dto source,Class<T> destinationClass) throws EntityNotFoundException, BadEntityException;
    public <T extends Dto> T mapToDto(E source, Class<T> destinationClass);
}
