package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver;

import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Getter
/**
 *
 *  Resolves parent- or child-id's from an Dto to their mapped Entities.
 *
 *  These id fields must be annotated with on of these Annotations:
 *  {@link BiDirChildId}, {@link BiDirParentId},
 *  {@link UniDirParentId}, {@link UniDirChildId}
 *  {@link UniDirChildIdCollection}, {@link BiDirChildIdCollection}
 *
 *  The resolving of the ids is done, by calling {@link CrudService#findById(Serializable)} of the {@link CrudService}, that belongs to the Annotated Id's Entity Type.
 *  The needed CrudService is found with {@link CrudServiceLocator}.
 */
public abstract class EntityIdResolver<E,Dto> {

    private CrudServiceLocator crudServiceLocator;
    private Class<Dto> dtoClass;

    public EntityIdResolver(CrudServiceLocator crudServiceLocator, Class<Dto> dtoClass) {
        this.dtoClass = dtoClass;
        this.crudServiceLocator = crudServiceLocator;
    }

    public abstract void resolveEntityIds(E mappedEntity, Dto dto) throws DtoMappingException;

    public abstract void resolveDtoIds(Dto mappedDto, E entity);


    protected Object findEntityFromService(Map.Entry<Class, Serializable> entityClassToIdMapping) throws DtoMappingException {
        try {
            CrudService entityService = crudServiceLocator.find(entityClassToIdMapping.getKey());
            if(entityService==null){
                throw new IllegalArgumentException("No Service found for entityClass: " + entityClassToIdMapping.getKey().getSimpleName());
            }
            Optional optionalParent;
            try {
                Serializable id = entityClassToIdMapping.getValue();
                optionalParent = entityService.findById(id);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("ParentId: " + entityClassToIdMapping.getValue() + " was of wrong type for Service: " + entityService,e);
            }
            if (!optionalParent.isPresent()) {
                throw new EntityNotFoundException("No Parent of Type: " + entityClassToIdMapping.getKey().getSimpleName() + " found with id: " + entityClassToIdMapping.getValue());
            }
            return optionalParent.get();
        }catch (NoIdException|EntityNotFoundException e){
            throw new DtoMappingException(e);
        }

    }


}
