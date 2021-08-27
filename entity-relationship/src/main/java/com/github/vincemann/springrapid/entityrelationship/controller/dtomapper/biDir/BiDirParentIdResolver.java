package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.EntityIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildId;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Used by {@link IdResolvingDtoPostProcessor}.
 * Resolves {@link BiDirChildId} to corresponding {@link BiDirChildEntity}.
 * Sets {@link BiDirParent} as {@link BiDirChild}'s Parent > sets Backreference
 *
 * @see EntityIdResolver
 */
public class BiDirParentIdResolver extends EntityIdResolver<BiDirParent, BiDirParentDto> {

    public BiDirParentIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, BiDirParentDto.class);
    }

    public void injectEntitiesFromDtoIds(BiDirParent mappedBiDirParent, BiDirParentDto biDirParentDto) throws BadEntityException, EntityNotFoundException {
        //find and handle single Children

        Map<Class<BiDirChild>, Serializable> childTypeIdMappings = biDirParentDto.findBiDirChildIds();
        for (Map.Entry<Class<BiDirChild>, Serializable> entry : childTypeIdMappings.entrySet()) {
            Class entityClass = entry.getKey();
            Object child = findEntityFromService((Class<IdentifiableEntity>) entityClass, entry.getValue());
            resolveBiDirChildFromService(child, mappedBiDirParent);
        }
        //find and handle children collections
        Map<Class<BiDirChild>, Collection<Serializable>> childTypeIdCollectionMappings = biDirParentDto.findBiDirChildIdCollections();
        for (Map.Entry<Class<BiDirChild>, Collection<Serializable>> entry : childTypeIdCollectionMappings.entrySet()) {
            Collection<Serializable> idCollection = entry.getValue();
            for (Serializable id : idCollection) {
                Class entityClass = entry.getKey();
                Object child = findEntityFromService((Class<IdentifiableEntity>) entityClass, id);
                resolveBiDirChildFromService(child, mappedBiDirParent);
            }
        }
    }

    @Override
    public void injectDtoIdsFromEntity(BiDirParentDto mappedDto, BiDirParent serviceEntity) {
        for (BiDirChild biDirChild : serviceEntity.findSingleBiDirChildren()) {
            mappedDto.addBiDirChildId(biDirChild);
        }
        for (Collection<? extends BiDirChild> childrenCollection : serviceEntity.findBiDirChildCollections().keySet()) {
            for (BiDirChild biDirChild : childrenCollection) {
                mappedDto.addBiDirChildId(biDirChild);
            }
        }
    }

    private void resolveBiDirChildFromService(Object child, BiDirParent mappedBiDirParent) {
        try {
            BiDirChild biDirChild = ((BiDirChild) child);
            //set child of mapped parent
            mappedBiDirParent.linkBiDirChild(biDirChild);
            //backreference gets set in BiDirParentListener
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Found Child " + child + " is not of Type BiDirChild");
        }
    }

}
