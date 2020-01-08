package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.finder.CrudServiceFinder;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
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
 *  {@link io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildId}, {@link io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentId},
 *  {@link io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirParentId}, {@link io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirChildId}
 *  {@link io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirChildIdCollection}, {@link io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildIdCollection}
 *
 *  The resolving of the ids is done, by calling {@link io.github.vincemann.generic.crud.lib.service.CrudService#findById(Serializable)} of the {@link CrudService}, that belongs to the Annotated Id's Entity Type.
 *  The needed CrudService is found with {@link CrudServiceFinder}.
 */
public abstract class EntityIdResolver<E,Dto> {

    private CrudServiceFinder crudServiceFinder;
    private Class<Dto> dtoClass;

    public EntityIdResolver(CrudServiceFinder crudServiceFinder, Class<Dto> dtoClass) {
        this.dtoClass = dtoClass;
        this.crudServiceFinder=crudServiceFinder;
    }

    public abstract void resolveServiceEntityIds(E mappedEntity, Dto dto) throws EntityMappingException;

    public abstract void resolveDtoIds(Dto mappedDto, E entity);


    protected Object findEntityFromService(Map.Entry<Class, Serializable> entityClassToIdMapping) throws EntityMappingException {
        try {
            CrudService entityService = crudServiceFinder.getCrudServices().get(entityClassToIdMapping.getKey());
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
            throw new EntityMappingException(e);
        }

    }


}
