package com.github.vincemann.springrapid.autobidir.resolveid;


import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.RepositoryLocator;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

import java.io.Serializable;

/**
 *
 *  Interface for components resolving entity to id and vice versa
 *

 *
 *  The resolving of the ids is done, by calling {@link CrudService#findById(Serializable)} of the {@link CrudService},
 *  that belongs to the Annotated Id's Entity Type.
 *  The needed CrudService is found with {@link RepositoryLocator}.
 *
 * @see com.github.vincemann.springrapid.autobidir.resolveid.bidir.BiDirChildIdResolver
 * @see DelegatingEntityIdResolver
 */
public interface EntityIdResolver {


    boolean supports(Class<?> dtoClass);

    /**
     * Resolves Id's from entities set in {@param entity} and inject (set) them into {@param target} dto.
     * @param targetDto target dto, that will be modified in this process
     * @param entity source entity, from which entity will be taken and resolved to ids
     * @param fieldsToCheck optional: you can limit the fields scanned by giving only the relevant field names
     */
    void setResolvedEntities(IdAwareEntity entity, Object targetDto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException;

    /**
     * Resolve entities by id from {@param dto} and inject (set) them into {@param target} entity.
     * @param targetEntity target entity, that will be modified in this process
     * @param dto source dto, from which ids will be taken and resolved to entities
     */
    void setResolvedIds(Object dto, IdAwareEntity targetEntity, String... fieldsToCheck);



}
