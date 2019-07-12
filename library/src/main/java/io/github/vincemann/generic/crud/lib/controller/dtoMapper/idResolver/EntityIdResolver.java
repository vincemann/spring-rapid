package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.CrudServiceFinder;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class EntityIdResolver<ServiceE,Dto> {

    private Map<Class<? extends IdentifiableEntity>, CrudService> crudServiceMap;
    private Class<Dto> dtoClass;

    public EntityIdResolver(CrudServiceFinder crudServiceFinder, Class<Dto> dtoClass) {
        this.dtoClass = dtoClass;
        this.crudServiceMap = crudServiceFinder.getCrudServices();
    }

    public abstract void resolveServiceEntityIds(ServiceE mappedServiceEntity, Dto dto) throws EntityMappingException;

    public abstract void resolveDtoIds(Dto mappedDto, ServiceE serviceEntity);




    protected Object findEntityFromService(Map.Entry<Class, Serializable> entityIdToClassMapping) throws EntityMappingException {
        try {
            CrudService entityService = getCrudServiceMap().get(entityIdToClassMapping.getKey());
            if(entityService==null){
                throw new IllegalArgumentException("No Service found for entityClass: " + entityIdToClassMapping.getKey().getSimpleName());
            }
            Optional optionalParent;
            try {
                optionalParent = entityService.findById(entityIdToClassMapping.getValue());
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("ParentId: " + entityIdToClassMapping.getValue() + " was of wrong type for Service: " + entityService,e);
            }
            if (!optionalParent.isPresent()) {
                throw new EntityNotFoundException("No Parent of Type: " + entityIdToClassMapping.getKey().getSimpleName() + " found with id: " + entityIdToClassMapping.getValue());
            }
            return optionalParent.get();
        }catch (NoIdException|EntityNotFoundException e){
            throw new EntityMappingException(e);
        }

    }


}
