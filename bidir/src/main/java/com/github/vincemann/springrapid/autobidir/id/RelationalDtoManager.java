package com.github.vincemann.springrapid.autobidir.id;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

/**
 * Component for id <-> entity resolving process.
 * Can find all entities from entity, convert them to ids and inject into target dto and vice versa:
 * Can find all ids from dto, convert them to respective entities and inject into target entity.
 * Also takes bidir relationships into account.
 *
 * @see RelationalDtoManagerImpl
 *
 */
public interface RelationalDtoManager {


    /**
     * Resolves Id's from entities set in {@param entity} and inject (set) them into {@param target} dto.
     * @param target target dto, that will be modified in this process
     * @param entity source entity, from which entity will be taken and resolved to ids
     * @param fieldsToCheck optional: you can limit the fields scanned by giving only the relevant field names
     */
    public void resolveIds(Object target, IdentifiableEntity<?> entity, String... fieldsToCheck);

    /**
     * Resolve entities by id from {@param dto} and inject (set) them into {@param target} entity.
     * @param target target entity, that will be modified in this process
     * @param dto source dto, from which ids will be taken and resolved to entities
     */
    public void resolveEntities(IdentifiableEntity<?> target, Object dto, String... fieldsToCheck) throws EntityNotFoundException, BadEntityException;
}
