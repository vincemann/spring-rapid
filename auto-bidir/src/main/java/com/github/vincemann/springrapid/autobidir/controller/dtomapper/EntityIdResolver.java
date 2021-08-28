package com.github.vincemann.springrapid.autobidir.controller.dtomapper;


import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;

@Getter
/**
 *
 *  Resolves parent- or child-id's from a Dto to their mapped Entities.
 *
 *  These id fields must be annotated with on of these Annotations:
 *  {@link BiDirChildId}, {@link BiDirParentId},
 *  {@link UniDirParentId}, {@link UniDirChildId}
 *  {@link UniDirChildIdCollection}, {@link BiDirChildIdCollection}
 *
 *  The resolving of the ids is done, by calling {@link CrudService#findById(Serializable)} of the {@link CrudService}, that belongs to the Annotated Id's Entity Type.
 *  The needed CrudService is found with {@link CrudServiceLocator}.
 *
 */
@LogInteraction(value = Severity.TRACE)
//@LogConfig(ignoreSetters = true, ignoreGetters = true)
public abstract class EntityIdResolver<E, Dto> implements AopLoggable {

    private CrudServiceLocator crudServiceLocator;
    private Class<Dto> dtoClass;

    public EntityIdResolver(CrudServiceLocator crudServiceLocator, Class<Dto> dtoClass) {
        this.dtoClass = dtoClass;
        this.crudServiceLocator = crudServiceLocator;
    }

    /**
     * Resolve entities by id from dto and inject (set) them into target Entity
     * -> target entity now has all entities set
     */
    public abstract void injectEntitiesFromDtoIds(E mappedEntity, Dto dto) throws BadEntityException, EntityNotFoundException;

    /**
     * Resolve Id's from entities in entity and inject (set) ids into target Dto
     */
    public abstract void injectDtoIdsFromEntity(Dto mappedDto, E entity);


    protected <T> T findEntityFromService(Class<IdentifiableEntity> entityClass, Serializable id) throws EntityNotFoundException, BadEntityException {
        CrudService entityService = crudServiceLocator.find(entityClass);
        if (entityService == null) {
            throw new IllegalArgumentException("No Service found for entityClass: " + entityClass.getSimpleName());
        }
        Optional optionalParent;
        try {
            optionalParent = entityService.findById(id);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("ParentId: " + id + " was of wrong type for Service: " + entityService, e);
        }
        VerifyEntity.isPresent(optionalParent, "No Parent of Type: " +entityClass.getSimpleName() + " found with id: " + id);
        return (T) optionalParent.get();

    }


}
