package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper;

import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.controller.dto.mapper.EntityDtoPostProcessor;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoEntityPostProcessor;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.entityrelationship.dto.DirDto;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves id fields, referencing parent/child entities.
 * The id resolving is done by the given {@link EntityIdResolver}s.
 *
 * @see EntityIdResolver
 */
@Order(1000)
@Transactional
public class IdResolvingDtoPostProcessor implements DtoPostProcessor<Object, IdentifiableEntity<?>>{

    private List<EntityIdResolver> entityIdResolvers;

    public IdResolvingDtoPostProcessor(List<EntityIdResolver> entityIdResolvers) {
        this.entityIdResolvers = entityIdResolvers;
    }

    @Override
    public boolean supports(Class<?>entityClass, Class<?> dtoClass) {
        return DirDto.class.isAssignableFrom(dtoClass);
    }

    @Override
    public void postProcessDto(Object dto, IdentifiableEntity<?> entity) {
        //yet unfinished
        List<EntityIdResolver> entityIdResolvers = findResolvers(dto.getClass());
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            entityIdResolver.resolveDtoIds(dto, entity);
        }
    }

    @Override
    public void postProcessEntity(IdentifiableEntity<?> entity, Object dto) throws BadEntityException, EntityNotFoundException {
        //yet unfinished
        List<EntityIdResolver> entityIdResolvers = findResolvers(dto.getClass());
        for (EntityIdResolver resolver : entityIdResolvers) {
            resolver.resolveEntityIds(entity, dto);
        }

    }

    private List<EntityIdResolver> findResolvers(Class<?> dstClass) {
        List<EntityIdResolver> resolvers = new ArrayList<>();
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            if (entityIdResolver.getDtoClass().isAssignableFrom(dstClass)) {
                resolvers.add(entityIdResolver);
            }
        }
        if (resolvers.isEmpty())
            throw new IllegalArgumentException("No " + EntityIdResolver.class.getSimpleName() + " found for dstClass: " + dstClass.getSimpleName());
        return resolvers;
    }
}
