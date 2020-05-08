package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver;

import io.github.vincemann.springrapid.core.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto;
import io.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildDto;
import io.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Maps an {@link IdentifiableEntity} to its Dto and vice versa using {@link org.modelmapper.ModelMapper} AND resolves
 * id fields, referencing parent/child entities.
 * The id resolving is done by the given {@link EntityIdResolver}s.
 *
 * @see EntityIdResolver
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@Transactional
public class IdResolvingDtoMapper extends BasicDtoMapper {

    private List<EntityIdResolver> entityIdResolvers;

    public IdResolvingDtoMapper(List<EntityIdResolver> entityIdResolvers) {
        this.entityIdResolvers = entityIdResolvers;
    }

    @Override
    public boolean isDtoClassSupported(Class<?> clazz) {
        return BiDirChildDto.class.isAssignableFrom(clazz) ||
                BiDirParentDto.class.isAssignableFrom(clazz) ||
                UniDirParentDto.class.isAssignableFrom(clazz) ||
                UniDirChildDto.class.isAssignableFrom(clazz);
    }

    @Override
    public <E extends IdentifiableEntity<?>> E mapToEntity(Object dto, Class<E> destinationClass) throws DtoMappingException {
        E mappingResult = super.mapToEntity(dto, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(dto.getClass());
        entityIdResolver.resolveEntityIds(mappingResult, dto);
        //is now finished
        return mappingResult;
    }

    @Override
    public <Dto> Dto mapToDto(IdentifiableEntity<?> entity, Class<Dto> destinationClass) throws DtoMappingException {
        Dto mappingResult = super.mapToDto(entity, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(destinationClass);
        entityIdResolver.resolveDtoIds(mappingResult, entity);
        //is now finished
        return mappingResult;
    }

    private EntityIdResolver findResolver(Class<?> dstClass) throws DtoMappingException {
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            if (entityIdResolver.getDtoClass().isAssignableFrom(dstClass)) {
                return entityIdResolver;
            }
        }
        throw new DtoMappingException("No "+EntityIdResolver.class.getSimpleName() + " found for dstClass: " + dstClass.getSimpleName());
    }
}
