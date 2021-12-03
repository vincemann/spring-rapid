package com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir;


import com.github.vincemann.springrapid.autobidir.RelationalDtoManager;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.EntityIdResolver;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.IdResolvingDtoPostProcessor;

import com.github.vincemann.springrapid.autobidir.dto.child.annotation.BiDirChildId;


import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType.BiDirChildDto;
import static com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType.BiDirParentDto;

/**
 * Used by {@link IdResolvingDtoPostProcessor}.
 * Resolves {@link BiDirChildId} to corresponding {@link BiDirChildEntity}.
 * Sets BiDirParent as BiDirChild's Parent > sets Backreference
 *
 * @see EntityIdResolver
 */
public class BiDirParentIdResolver extends EntityIdResolver {

    public BiDirParentIdResolver() {
        super(BiDirParentDto);
    }

    @Override
    public void setResolvedEntities(IdentifiableEntity mappedBiDirParent, Object biDirParentDto) throws BadEntityException, EntityNotFoundException {
        //find and handle single Children

        Map<Class<IdentifiableEntity>, Serializable> childTypeIdMappings = relationalDtoManager.findBiDirChildIds(biDirParentDto);
        for (Map.Entry<Class<IdentifiableEntity>, Serializable> entry : childTypeIdMappings.entrySet()) {
            Class entityClass = entry.getKey();
            IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>) entityClass, entry.getValue());
            resolveBiDirChildFromService(child, mappedBiDirParent);
        }
        //find and handle children collections
        Map<Class<IdentifiableEntity>, Collection<Serializable>> childTypeIdCollectionMappings = relationalDtoManager.findBiDirChildIdCollections(biDirParentDto);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : childTypeIdCollectionMappings.entrySet()) {
            Collection<Serializable> idCollection = entry.getValue();
            for (Serializable id : idCollection) {
                Class entityClass = entry.getKey();
                IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>) entityClass, id);
                resolveBiDirChildFromService(child, mappedBiDirParent);
            }
        }
    }

    @Override
    public void setResolvedIds(Object mappedDto, IdentifiableEntity serviceEntity) {
        for (IdentifiableEntity biDirChild : relationalEntityManager.findSingleBiDirChildren(serviceEntity)) {
            relationalDtoManager.addBiDirChildId(biDirChild,mappedDto);
        }
        for (Collection<? extends IdentifiableEntity> childrenCollection : relationalEntityManager.findBiDirChildCollections(serviceEntity).keySet()) {
            for (IdentifiableEntity biDirChild : childrenCollection) {
                relationalDtoManager.addBiDirChildId(biDirChild,mappedDto);
            }
        }
    }

    private void resolveBiDirChildFromService(IdentifiableEntity child, IdentifiableEntity mappedBiDirParent) {
        try {
            //set child of mapped parent
            relationalEntityManager.linkBiDirChild(mappedBiDirParent, child);
            //backreference gets set in BiDirParentListener
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Found Child " + child + " is not of Type BiDirChild");
        }
    }

}
