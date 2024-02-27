package com.github.vincemann.springrapid.autobidir.resolveid.bidir;


import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.autobidir.resolveid.AbstractRelationalEntityIdResolver;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.autobidir.resolveid.EntityIdResolver;


import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent.BiDirParentId;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.github.vincemann.springrapid.autobidir.resolveid.RelationalDtoType.BiDirChildDto;

/**
 * Resolves {@link BiDirParentId} to corresponding {@link BiDirParentEntity}.
 * Adds mapped BiDirChild to parents found via {@link RelationalEntityManagerUtil#findAllBiDirParents(IdentifiableEntity, String...)}'s
 * -> sets backref
 *
 * @see EntityIdResolver
 */
public class BiDirChildIdResolver extends AbstractRelationalEntityIdResolver {

    public BiDirChildIdResolver() {
        super(BiDirChildDto);
    }

    @Override
    public void setResolvedEntities(IdentifiableEntity entity, Object target, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        //find all parents by id and map them to child
        Map<Class<IdentifiableEntity>, Collection<Serializable>> parentIds = getRelationalDtoManagerUtil().findAllBiDirParentIds(target);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : parentIds.entrySet()) {
            Class<IdentifiableEntity> entityClass = entry.getKey();
            Collection<Serializable> parentIdCollection = entry.getValue();
            for (Serializable id : parentIdCollection) {
                IdentifiableEntity parent = findEntityFromService(entityClass, id);
                getRelationalEntityManagerUtil().linkBiDirParent(entity, parent);
            }
        }
    }

    @Override
    public void setResolvedIds(Object dto, IdentifiableEntity target, String... fieldsToCheck) {
        for (IdentifiableEntity parent : getRelationalEntityManagerUtil().findAllBiDirParents(target,fieldsToCheck)) {
            getRelationalDtoManagerUtil().addBiDirParentId(parent, dto);
        }
    }
}
