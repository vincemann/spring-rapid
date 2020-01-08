package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildDto;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentDto;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirChildDto;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirParentDto;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps an {@link IdentifiableEntity} to its Dto and vice versa using {@link org.modelmapper.ModelMapper} AND resolving
 * id fields, referencing parent/child entities.
 * The id resolving is done by the given {@link EntityIdResolver}s.
 */
@Component
public class IdResolvingDtoMapper extends BasicDtoMapper {

    private List<EntityIdResolver> entityIdResolvers;

    @Autowired
    public IdResolvingDtoMapper(List<EntityIdResolver> entityIdResolvers) {
        this.entityIdResolvers = entityIdResolvers;
    }

    @Override
    public boolean isDtoClassSupported(Class<? extends IdentifiableEntity> clazz) {
        return BiDirChildDto.class.isAssignableFrom(clazz) ||
                BiDirParentDto.class.isAssignableFrom(clazz) ||
                UniDirParentDto.class.isAssignableFrom(clazz) ||
                UniDirChildDto.class.isAssignableFrom(clazz);
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
    public <Dto extends IdentifiableEntity> Dto mapEntityToDto(Object source, Class<Dto> destinationClass) throws EntityMappingException {
        Dto mappingResult = super.mapEntityToDto(source, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(destinationClass);
        entityIdResolver.resolveDtoIds(mappingResult, source);
        //is now finished
        return mappingResult;
    }

    private EntityIdResolver findResolver(Class dstClass) throws EntityMappingException {
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            if (entityIdResolver.getDtoClass().isAssignableFrom(dstClass)) {
                return entityIdResolver;
            }
        }
        throw new EntityMappingException("No "+EntityIdResolver.class.getSimpleName() + " found for dstClass: " + dstClass.getSimpleName());
    }
}
