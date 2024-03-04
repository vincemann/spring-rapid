package com.github.vincemann.springrapid.autobidir.resolveid;


import com.github.vincemann.springrapid.autobidir.resolveid.bidir.BiDirParentIdResolver;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;


import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.github.vincemann.springrapid.autobidir.resolveid.RelationalDtoType.UniDirParentDto;

/**
 * Same as {@link BiDirParentIdResolver} but without backref setting and for UniDirParent
 *
 * @see EntityIdResolver
 */
public class UniDirParentIdResolver extends AbstractRelationalEntityIdResolver {

    public UniDirParentIdResolver() {
        super(UniDirParentDto);
    }

    @Override
    public void setResolvedEntities(IdentifiableEntity entity, Object targetDto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        //find all children by id and map them to parent
        Map<Class<IdentifiableEntity>, Collection<Serializable>> childIds = getRelationalDtoManagerUtil().findAllUniDirChildIds(targetDto);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : childIds.entrySet()) {
            Collection<Serializable> childIdCollection = entry.getValue();
            for (Serializable id : childIdCollection) {
                Class<IdentifiableEntity> entityClass = entry.getKey();
                IdentifiableEntity child = findEntityFromService(entityClass, id);
                getRelationalEntityManagerUtil().linkUniDirChild(entity,child);
            }
        }
    }

    @Override
    public void setResolvedIds(Object dto, IdentifiableEntity targetEntity, String... fieldsToCheck) {
        for (IdentifiableEntity child : getRelationalEntityManagerUtil().findAllUniDirChildren(targetEntity,fieldsToCheck)) {
            getRelationalDtoManagerUtil().addUniDirChildId(child, dto);
        }
    }
}
