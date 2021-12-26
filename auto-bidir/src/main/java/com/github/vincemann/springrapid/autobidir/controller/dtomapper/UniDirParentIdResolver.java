package com.github.vincemann.springrapid.autobidir.controller.dtomapper;


import com.github.vincemann.springrapid.autobidir.RelationalDtoManager;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.BiDirParentIdResolver;



import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType.BiDirParentDto;
import static com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType.UniDirParentDto;

/**
 * Same as {@link BiDirParentIdResolver} but without backref setting and for : UniDirParent
 *
 * @see EntityIdResolver
 */
public class UniDirParentIdResolver extends EntityIdResolver {

    public UniDirParentIdResolver() {
        super(UniDirParentDto);
    }

    public void setResolvedEntities(IdentifiableEntity mappedUniDirParent, Object uniDirParentDto) throws BadEntityException, EntityNotFoundException {
        //find and handle single Children
        Map<Class<IdentifiableEntity>, Serializable> childTypeIdMappings = relationalDtoManager.findUniDirChildIds(uniDirParentDto);
        for (Map.Entry<Class<IdentifiableEntity>, Serializable> entry : childTypeIdMappings.entrySet()) {
            Class entityClass = entry.getKey();
            IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>) entityClass, entry.getValue());
            relationalEntityManager.linkUniDirChild(mappedUniDirParent,child);
        }
        //find and handle children collections
        Map<Class<IdentifiableEntity>, Collection<Serializable>> childTypeIdCollectionMappings = relationalDtoManager.findUniDirChildIdCollections(uniDirParentDto);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : childTypeIdCollectionMappings.entrySet()) {
            Collection<Serializable> idCollection = entry.getValue();
            for (Serializable id : idCollection) {
                Class entityClass = entry.getKey();
                IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>)entityClass, id);
                relationalEntityManager.linkUniDirChild(mappedUniDirParent,child);
            }
        }
    }

    @Override
    public void setResolvedIds(Object mappedDto, IdentifiableEntity serviceEntity) {
        for (IdentifiableEntity child : relationalEntityManager.findSingleUniDirChildren(serviceEntity)) {
            relationalDtoManager.addUniDirChildId(child, mappedDto);
        }
        for (Collection<IdentifiableEntity> childrenCollection : relationalEntityManager.findUniDirChildCollections(serviceEntity).values()) {
            for (IdentifiableEntity child : childrenCollection) {
                relationalDtoManager.addUniDirChildId(child,mappedDto);
            }
        }
    }
}
