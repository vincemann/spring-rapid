package com.github.vincemann.springrapid.autobidir.id;


import com.github.vincemann.springrapid.autobidir.id.biDir.BiDirParentIdResolver;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;


import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.github.vincemann.springrapid.autobidir.id.RelationalDtoType.UniDirParentDto;

/**
 * Same as {@link BiDirParentIdResolver} but without backref setting and for : UniDirParent
 *
 * @see EntityIdResolver
 */
public class UniDirParentIdResolver extends AbstractRelationalEntityIdResolver {

    public UniDirParentIdResolver() {
        super(UniDirParentDto);
    }

    @Override
    public void setResolvedEntities(IdentifiableEntity mappedUniDirParent, Object uniDirParentDto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        //find all children by id and map them to parent
        Map<Class<IdentifiableEntity>, Collection<Serializable>> childIds = getRelationalDtoManagerUtil().findAllUniDirChildIds(uniDirParentDto);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : childIds.entrySet()) {
            Collection<Serializable> childIdCollection = entry.getValue();
            for (Serializable id : childIdCollection) {
                Class<IdentifiableEntity> entityClass = entry.getKey();
                IdentifiableEntity child = findEntityFromService(entityClass, id);
                getRelationalEntityManagerUtil().linkUniDirChild(mappedUniDirParent,child);
            }
        }
    }

    @Override
    public void setResolvedIds(Object mappedDto, IdentifiableEntity serviceEntity, String... fieldsToCheck) {
        for (IdentifiableEntity child : getRelationalEntityManagerUtil().findAllUniDirChildren(serviceEntity,fieldsToCheck)) {
            getRelationalDtoManagerUtil().addUniDirChildId(child, mappedDto);
        }
    }
}
