package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.util.List;

/**
 * Maps an {@link IdentifiableEntity} to its Dto and vice versa using {@link org.modelmapper.ModelMapper} AND resolving
 * id fields, referencing parent/child entities.
 * The id resolving is done by the given {@link EntityIdResolver}s.
 */
public class IdResolvingDtoMapper extends BasicDtoMapper {

    private List<EntityIdResolver> entityIdResolvers;


    public IdResolvingDtoMapper(List<EntityIdResolver> entityIdResolvers) {
        this.entityIdResolvers = entityIdResolvers;
    }

    @Override
    public <E extends IdentifiableEntity> E mapDtoToEntity(Object source, Class<E> destinationClass) throws EntityMappingException {
        E mappingResult = super.mapDtoToEntity(source, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(source.getClass());
        entityIdResolver.resolveServiceEntityIds(mappingResult, source);
        //is now finished
        return mappingResult;
    }

    @Override
    public <Dto extends IdentifiableEntity> Dto mapEntityToDto(Object source, Class<Dto> destinationClass) {
        Dto mappingResult = super.mapEntityToDto(source, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(destinationClass);
        entityIdResolver.resolveDtoIds(mappingResult, source);
        //is now finished
        return mappingResult;
    }

    private EntityIdResolver findResolver(Class dstClass) {
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            if (entityIdResolver.getDtoClass().isAssignableFrom(dstClass)) {
                return entityIdResolver;
            }
        }
        throw new IllegalArgumentException("No "+EntityIdResolver.class.getSimpleName() + " found for dstClass: " + dstClass.getSimpleName());
    }
}
