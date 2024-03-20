package com.github.vincemann.springrapid.autobidir.resolveid;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Delegates to {@link EntityIdResolver}s, that do the heavy lifting.
 * If you need customized resolving, register your own {@link EntityIdResolver} or overwrite existing.
 *
 * @see com.github.vincemann.springrapid.autobidir.resolveid.bidir.BiDirParentIdResolver
 * @see com.github.vincemann.springrapid.autobidir.resolveid.bidir.BiDirChildIdResolver
 * @see UniDirParentIdResolver
 */
public class DelegatingEntityIdResolver {

    private List<EntityIdResolver> resolvers;

    public DelegatingEntityIdResolver(List<EntityIdResolver> resolvers) {
        this.resolvers = resolvers;
    }


    public void setResolvedEntities(IdAwareEntity entity, Object targetDto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        List<EntityIdResolver> resolvers = findMatchingResolvers(targetDto.getClass());
        for (EntityIdResolver resolver : resolvers) {
            resolver.setResolvedEntities(entity, targetDto,fieldsToCheck);
        }
    }

    public void setResolvedIds(Object dto, IdAwareEntity targetEntity, String... fieldsToCheck) {
        List<EntityIdResolver> resolvers = findMatchingResolvers(dto.getClass());
        for (EntityIdResolver entityIdResolver : resolvers) {
            entityIdResolver.setResolvedIds(dto, targetEntity, fieldsToCheck);
        }
    }


    public List<EntityIdResolver> findMatchingResolvers(Class<?> dtoClass) {
        List<EntityIdResolver> resolvers = new ArrayList<>();
        for (EntityIdResolver resolver : this.resolvers) {
            if (resolver.supports(dtoClass)) {
                resolvers.add(resolver);
            }
        }
        return resolvers;
    }

}
