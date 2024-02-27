package com.github.vincemann.springrapid.autobidir.id;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Default impl of {@link RelationalDtoManager}.
 * Delegates to {@link EntityIdResolver}s, that do the heavy lifting.
 * If you need customized resolving, register your own {@link EntityIdResolver} or overwrite existing.
 *
 * @see com.github.vincemann.springrapid.autobidir.id.biDir.BiDirParentIdResolver
 * @see com.github.vincemann.springrapid.autobidir.id.biDir.BiDirChildIdResolver
 * @see UniDirParentIdResolver
 */
public class RelationalDtoManagerImpl implements RelationalDtoManager {

    private RelationalDtoManagerUtil helper;
    private List<EntityIdResolver> resolvers;

    public RelationalDtoManagerImpl(List<EntityIdResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public void resolveIds(Object target, IdentifiableEntity<?> entity, String... fieldsToCheck) {
        List<EntityIdResolver> entityIdResolvers = findMatchingResolvers(target.getClass());
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            entityIdResolver.setResolvedIds(target, entity, fieldsToCheck);
        }
    }

    @Override
    public void resolveEntities(IdentifiableEntity<?> target, Object dto, String... fieldsToCheck) throws EntityNotFoundException, BadEntityException {
        List<EntityIdResolver> entityIdResolvers = findMatchingResolvers(dto.getClass());
        for (EntityIdResolver resolver : entityIdResolvers) {
            resolver.setResolvedEntities(target, dto);
        }
    }

    public List<EntityIdResolver> findMatchingResolvers(Class<?> dstClass) {
        List<EntityIdResolver> resolvers = new ArrayList<>();
        Set<RelationalDtoType> relationalDtoTypes = helper.inferTypes(dstClass);
        for (RelationalDtoType relationalDtoType : relationalDtoTypes) {
            for (EntityIdResolver entityIdResolver : this.resolvers) {
                if (entityIdResolver.getDtoType().equals(relationalDtoType)) {
                    resolvers.add(entityIdResolver);
                }
            }
        }
        return resolvers;
    }


    @Autowired
    public void setHelper(RelationalDtoManagerUtil helper) {
        this.helper = helper;
    }
}
