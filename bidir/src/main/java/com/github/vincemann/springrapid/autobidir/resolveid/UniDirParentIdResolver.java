package com.github.vincemann.springrapid.autobidir.resolveid;


import com.github.vincemann.springrapid.autobidir.resolveid.bidir.BiDirParentIdResolver;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
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
    public void setResolvedEntities(IdAwareEntity entity, Object targetDto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        //find all children by id and map them to parent
        Map<Class<IdAwareEntity>, Collection<Serializable>> childIds = getRelationalDtoManagerUtil().findAllUniDirChildIds(targetDto,fieldsToCheck);
        for (Map.Entry<Class<IdAwareEntity>, Collection<Serializable>> entry : childIds.entrySet()) {
            Collection<Serializable> childIdCollection = entry.getValue();
            for (Serializable id : childIdCollection) {
                Class<IdAwareEntity> entityClass = entry.getKey();
                IdAwareEntity child = findEntityFromService(entityClass, id);
                getRelationalEntityManagerUtil().linkUniDirChild(entity,child);
            }
        }
    }

    @Override
    public void setResolvedIds(Object dto, IdAwareEntity targetEntity, String... fieldsToCheck) {
        for (IdAwareEntity child : getRelationalEntityManagerUtil().findAllUniDirChildren(targetEntity,fieldsToCheck)) {
            getRelationalDtoManagerUtil().addUniDirChildId(child, dto);
        }
    }
}
