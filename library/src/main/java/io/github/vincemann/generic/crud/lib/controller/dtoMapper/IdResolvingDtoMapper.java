package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;

import java.io.Serializable;
import java.util.List;

/**
 * Maps an {@link IdentifiableEntity} to its Dto and vice versa using {@link org.modelmapper.ModelMapper} AND resolving
 * id fields, referencing parent/child entities.
 * The id resolving is done by the given {@link EntityIdResolver}s.
 *
 *
 *
 */
public class IdResolvingDtoMapper extends BasicDtoMapper {

    private List<EntityIdResolver> entityIdResolvers;

    public IdResolvingDtoMapper(List<EntityIdResolver> entityIdResolvers) {
        this.entityIdResolvers = entityIdResolvers;
    }

    @Override
    public <ServiceE extends IdentifiableEntity> ServiceE mapDtoToServiceEntity(Object source, Class<ServiceE> destinationClass) throws EntityMappingException {
        ServiceE mappingResult =  super.mapDtoToServiceEntity(source, destinationClass);
        //yet unfinished
        for (EntityIdResolver entityIdResolver : entityIdResolvers){
            if(entityIdResolver.getDtoClass().isAssignableFrom(source.getClass())){
                entityIdResolver.resolveServiceEntityIds(mappingResult,source);
            }
        }
        //is now finished
        return mappingResult;
    }

    @Override
    public <Dto extends IdentifiableEntity> Dto mapServiceEntityToDto(Object source, Class<Dto> destinationClass){
        Dto mappingResult =  super.mapServiceEntityToDto(source, destinationClass);
        //yet unfinished
        for (EntityIdResolver entityIdResolver : entityIdResolvers){
            if(entityIdResolver.getDtoClass().isAssignableFrom(destinationClass)){
                entityIdResolver.resolveDtoIds(mappingResult,source);
            }
        }
        //is now finished
        return mappingResult;
    }
}
