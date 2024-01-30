package com.github.vincemann.springrapid.core.controller.dto.map;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

/**
 * Maps a Dto to an Entity, or vice versa
 */
@LogInteraction
//@LogException
public interface DtoMapper<E extends IdentifiableEntity<?>,Dto> extends AopLoggable {

    @LogInteraction(disabled = true)
    public boolean supports(Class<?> dtoClass);

    public <T extends E> T mapToEntity(Dto source,Class<T> destinationClass) throws EntityNotFoundException, BadEntityException;
    public <T extends Dto> T mapToDto(E source, Class<T> destinationClass,String... fieldsToMap) throws BadEntityException;

//    <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass, Set<String> propertiesToMap);
}
