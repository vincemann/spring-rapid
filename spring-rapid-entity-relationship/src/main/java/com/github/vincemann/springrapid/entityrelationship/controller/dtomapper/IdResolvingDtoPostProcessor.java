package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper;

import com.github.vincemann.springrapid.core.controller.dtoMapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentDto;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
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
@Order(1000)
@Transactional
public class IdResolvingDtoPostProcessor implements DtoPostProcessor<Object, IdentifiableEntity<?>> {

    private List<EntityIdResolver> entityIdResolvers;
    @Getter
    @Setter
    private ModelMapper modelMapper;

    public IdResolvingDtoPostProcessor(List<EntityIdResolver> entityIdResolvers) {
        this.entityIdResolvers = entityIdResolvers;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public boolean supports(Class<?>entityClass, Class<?> dtoClass) {
        return (BiDirChildDto.class.isAssignableFrom(dtoClass) ||
                BiDirParentDto.class.isAssignableFrom(dtoClass) ||
                UniDirParentDto.class.isAssignableFrom(dtoClass) ||
                UniDirChildDto.class.isAssignableFrom(dtoClass));
    }

    @Override
    public void postProcessDto(Object dto, IdentifiableEntity<?> entity) {
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(dto.getClass());
        entityIdResolver.resolveDtoIds(dto, entity);
    }

    @Override
    public void postProcessEntity(IdentifiableEntity<?> entity, Object dto) throws BadEntityException, EntityNotFoundException {
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(dto.getClass());
        entityIdResolver.resolveEntityIds(entity, dto);

    }

    private EntityIdResolver findResolver(Class<?> dstClass) {
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            if (entityIdResolver.getDtoClass().isAssignableFrom(dstClass)) {
                return entityIdResolver;
            }
        }
        throw new IllegalArgumentException("No " + EntityIdResolver.class.getSimpleName() + " found for dstClass: " + dstClass.getSimpleName());
    }
}
