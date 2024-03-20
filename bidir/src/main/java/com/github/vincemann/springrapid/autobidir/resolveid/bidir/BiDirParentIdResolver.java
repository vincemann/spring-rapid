package com.github.vincemann.springrapid.autobidir.resolveid.bidir;


import com.github.vincemann.springrapid.autobidir.resolveid.AbstractRelationalEntityIdResolver;
import com.github.vincemann.springrapid.autobidir.resolveid.DelegatingEntityIdResolver;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.autobidir.resolveid.EntityIdResolver;

import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.BiDirChildId;


import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.github.vincemann.springrapid.autobidir.resolveid.RelationalDtoType.BiDirParentDto;

/**
 * Resolves {@link BiDirChildId} to corresponding {@link BiDirChildEntity}.
 * Handles bidir backref setting.
 *
 * @see EntityIdResolver
 * @see DelegatingEntityIdResolver
 */
public class BiDirParentIdResolver extends AbstractRelationalEntityIdResolver {

    public BiDirParentIdResolver() {
        super(BiDirParentDto);
    }

    @Override
    public void setResolvedEntities(IdAwareEntity entity, Object targetDto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        // find all children by id and map them to parent
        Map<Class<IdAwareEntity>, Collection<Serializable>> childIds = getRelationalDtoManagerUtil().findAllBiDirChildIds(targetDto,fieldsToCheck);
        for (Map.Entry<Class<IdAwareEntity>, Collection<Serializable>> entry : childIds.entrySet()) {
            Collection<Serializable> childIdCollection = entry.getValue();
            for (Serializable id : childIdCollection) {
                Class entityClass = entry.getKey();
                IdAwareEntity child = findEntityFromService((Class<IdAwareEntity>) entityClass, id);
                getRelationalEntityManagerUtil().linkBiDirChild(entity, child);
            }
        }
    }

    @Override
    public void setResolvedIds(Object dto, IdAwareEntity targetEntity, String... fieldsToCheck) {
        for (IdAwareEntity child : getRelationalEntityManagerUtil().findAllBiDirChildren(targetEntity,fieldsToCheck)) {
            getRelationalDtoManagerUtil().addBiDirChildId(child, dto);
        }
    }

}
