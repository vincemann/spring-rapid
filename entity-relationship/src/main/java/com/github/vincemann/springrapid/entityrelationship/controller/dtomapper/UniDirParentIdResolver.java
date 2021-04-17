package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.BiDirParentIdResolver;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.UniDirParentDto;
import com.github.vincemann.springrapid.entityrelationship.model.child.UniDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Same as {@link BiDirParentIdResolver} but without backref setting and for : {@link UniDirParent}
 *
 * @see EntityIdResolver
 */
public class UniDirParentIdResolver extends EntityIdResolver<UniDirParent, UniDirParentDto> {

    public UniDirParentIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, UniDirParentDto.class);
    }

    public void resolveEntityIds(UniDirParent mappedUniDirParent, UniDirParentDto uniDirParentDto) throws BadEntityException, EntityNotFoundException {
        //find and handle single Children
        Map<Class<UniDirChild>, Serializable> childTypeIdMappings = uniDirParentDto.findAllUniDirChildIds();
        for (Map.Entry<Class<UniDirChild>, Serializable> entry : childTypeIdMappings.entrySet()) {
            Class entityClass = entry.getKey();
            UniDirChild child = findEntityFromService((Class<IdentifiableEntity>) entityClass, entry.getValue());
            mappedUniDirParent.linkUniDirChild(child);
        }
        //find and handle children collections
        Map<Class<UniDirChild>, Collection<Serializable>> childTypeIdCollectionMappings = uniDirParentDto.findAllUniDirChildIdCollections();
        for (Map.Entry<Class<UniDirChild>, Collection<Serializable>> entry : childTypeIdCollectionMappings.entrySet()) {
            Collection<Serializable> idCollection = entry.getValue();
            for (Serializable id : idCollection) {
                Class entityClass = entry.getKey();
                UniDirChild child = findEntityFromService((Class<IdentifiableEntity>)entityClass, id);
                mappedUniDirParent.linkUniDirChild(child);
            }
        }
    }

    @Override
    public void resolveDtoIds(UniDirParentDto mappedDto, UniDirParent serviceEntity) {
        for (UniDirChild child : serviceEntity.findSingleUniDirChildren()) {
            mappedDto.addUniDirChildsId(child);
        }
        for (Collection<UniDirChild> childrenCollection : serviceEntity.findUniDirChildCollections().keySet()) {
            for (UniDirChild child : childrenCollection) {
                mappedDto.addUniDirChildsId(child);
            }
        }
    }
}
