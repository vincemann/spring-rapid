package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver;

import io.github.vincemann.springrapid.core.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.exception.DtoMappingException;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto;
import io.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildDto;
import io.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentDto;

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
    public boolean isDtoClassSupported(Class<? extends IdentifiableEntity> clazz) {
        return BiDirChildDto.class.isAssignableFrom(clazz) ||
                BiDirParentDto.class.isAssignableFrom(clazz) ||
                UniDirParentDto.class.isAssignableFrom(clazz) ||
                UniDirChildDto.class.isAssignableFrom(clazz);
    }

    @Override
    public <E extends IdentifiableEntity> E mapToEntity(IdentifiableEntity source, Class<E> destinationClass) throws DtoMappingException {
        E mappingResult = super.mapToEntity(source, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(source.getClass());
        entityIdResolver.resolveEntityIds(mappingResult, source);
        //is now finished
        return mappingResult;
    }

    @Override
    public <Dto extends IdentifiableEntity> Dto mapToDto(IdentifiableEntity source, Class<Dto> destinationClass) throws DtoMappingException {
        Dto mappingResult = super.mapToDto(source, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(destinationClass);
        entityIdResolver.resolveDtoIds(mappingResult, source);
        //is now finished
        return mappingResult;
    }

    private EntityIdResolver findResolver(Class dstClass) throws DtoMappingException {
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            if (entityIdResolver.getDtoClass().isAssignableFrom(dstClass)) {
                return entityIdResolver;
            }
        }
        throw new DtoMappingException("No "+EntityIdResolver.class.getSimpleName() + " found for dstClass: " + dstClass.getSimpleName());
    }
}
