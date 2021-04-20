package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.EntityIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.child.BiDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Used by {@link IdResolvingDtoPostProcessor}.
 * Resolves {@link BiDirParentId} to corresponding {@link BiDirParentEntity}.
 * Adds mapped {@link BiDirChild} to {@link BiDirParent#findSingleBiDirChildren()}'s  -> sets Backreference
 *
 * @see EntityIdResolver
 */
public class BiDirChildIdResolver extends EntityIdResolver<BiDirChild, BiDirChildDto> {

    public BiDirChildIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, BiDirChildDto.class);
    }

    public void resolveEntityIds(BiDirChild mappedBiDirChild, BiDirChildDto biDirChildDto) throws BadEntityException, EntityNotFoundException {
        Map<Class<BiDirParent>, Serializable> parentTypeIdMappings = biDirChildDto.findBiDirParentIds();
        for (Map.Entry<Class<BiDirParent>, Serializable> entry : parentTypeIdMappings.entrySet()) {
            Class entityClass = entry.getKey();
            Object parent = findEntityFromService((Class<IdentifiableEntity>)entityClass, entry.getValue());
            try {
//                BiDirParent biDirParent = ((BiDirParent) parent);
//                //set parent of mapped child
//                mappedBiDirChild.linkBiDirParent(biDirParent);
//                //backreference gets set in BiDirChildListener
                resolveBiDirParentFromService(parent, mappedBiDirChild);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Found Parent " + parent + " is not of Type BiDirParent");
            }
        }

        //find and handle parent collections
        Map<Class<BiDirParent>, Collection<Serializable>> parentTypeIdCollectionMappings = biDirChildDto.findBiDirParentIdCollections();
        for (Map.Entry<Class<BiDirParent>, Collection<Serializable>> entry : parentTypeIdCollectionMappings.entrySet()) {
            Collection<Serializable> idCollection = entry.getValue();
            for (Serializable id : idCollection) {
                Class entityClass = entry.getKey();
                Object parent = findEntityFromService((Class<IdentifiableEntity>) entityClass, id);
                resolveBiDirParentFromService(parent, mappedBiDirChild);
            }
        }
    }

    private void resolveBiDirParentFromService(Object parent, BiDirChild mappedBiDirChild) {
        try {
            BiDirParent biDirParent = ((BiDirParent) parent);
            //set parent of mapped parent
            mappedBiDirChild.linkBiDirParent(biDirParent);
            //backreference gets set in BiDirParentListener
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Found Child " + parent + " is not of Type BiDirChild");
        }
    }

    @Override
    public void resolveDtoIds(BiDirChildDto mappedDto, BiDirChild serviceEntity) {
        for (BiDirParent biDirParent : serviceEntity.findSingleBiDirParents()) {
            mappedDto.addBiDirParentId(biDirParent);
        }
        for (Collection<? extends BiDirParent> parentCollection : serviceEntity.findBiDirParentCollections().keySet()) {
            for (BiDirParent biDirParent : parentCollection) {
                mappedDto.addBiDirParentId(biDirParent);
            }
        }
    }
}
