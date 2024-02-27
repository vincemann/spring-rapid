package com.github.vincemann.springrapid.autobidir.id.biDir;


import com.github.vincemann.springrapid.autobidir.id.AbstractRelationalEntityIdResolver;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.autobidir.id.EntityIdResolver;
import com.github.vincemann.springrapid.autobidir.id.IdResolvingDtoPostProcessor;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildId;


import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.github.vincemann.springrapid.autobidir.id.RelationalDtoType.BiDirParentDto;

/**
 * Resolves {@link BiDirChildId} to corresponding {@link BiDirChildEntity}.
 * Handles bidir backref setting.
 *
 * @see EntityIdResolver
 * @see com.github.vincemann.springrapid.autobidir.id.RelationalDtoManagerImpl
 */
public class BiDirParentIdResolver extends AbstractRelationalEntityIdResolver {

    public BiDirParentIdResolver() {
        super(BiDirParentDto);
    }

    @Override
    public void setResolvedEntities(IdentifiableEntity mappedBiDirParent, Object biDirParentDto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        // find all children by id and map them to parent
        Map<Class<IdentifiableEntity>, Collection<Serializable>> childIds = getRelationalDtoManagerUtil().findAllBiDirChildIds(biDirParentDto);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : childIds.entrySet()) {
            Collection<Serializable> childIdCollection = entry.getValue();
            for (Serializable id : childIdCollection) {
                Class entityClass = entry.getKey();
                IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>) entityClass, id);
                getRelationalEntityManagerUtil().linkBiDirChild(mappedBiDirParent, child);
            }
        }
    }

    @Override
    public void setResolvedIds(Object mappedDto, IdentifiableEntity serviceEntity, String... fieldsToCheck) {
        for (IdentifiableEntity child : getRelationalEntityManagerUtil().findAllBiDirChildren(serviceEntity,fieldsToCheck)) {
            getRelationalDtoManagerUtil().addBiDirChildId(child,mappedDto);
        }
    }

}
