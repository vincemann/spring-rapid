package com.github.vincemann.springrapid.autobidir.id.biDir;


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

        //find all children by id and map them to parent
        // create method for finding all child - Id mappings, then use findEntityFromService method to resolve entity then call relationalEntityManagerUtil.link...Entity(mappedEntity,relatedEntity);
        Map<Class<IdentifiableEntity>, Collection<Serializable>> childTypeIdCollectionMappings = relationalDtoManager.findAllBiDirChildIds(biDirParentDto);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : childTypeIdCollectionMappings.entrySet()) {
            Collection<Serializable> idCollection = entry.getValue();
            for (Serializable id : idCollection) {
                Class entityClass = entry.getKey();
                IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>) entityClass, id);
//                resolveBiDirChildFromService(child, mappedBiDirParent);
                relationalEntityManagerUtil.linkBiDirChild(mappedBiDirParent, child);
            }
        }


//        //find and handle single Children
//
//        Map<Class<IdentifiableEntity>, Serializable> childTypeIdMappings = relationalDtoManager.findBiDirChildIds(biDirParentDto);
//        for (Map.Entry<Class<IdentifiableEntity>, Serializable> entry : childTypeIdMappings.entrySet()) {
//            Class entityClass = entry.getKey();
//            IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>) entityClass, entry.getValue());
//            resolveBiDirChildFromService(child, mappedBiDirParent);
//        }
//        //find and handle children collections
//        Map<Class<IdentifiableEntity>, Collection<Serializable>> childTypeIdCollectionMappings = relationalDtoManager.findBiDirChildIdCollections(biDirParentDto);
//        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : childTypeIdCollectionMappings.entrySet()) {
//            Collection<Serializable> idCollection = entry.getValue();
//            for (Serializable id : idCollection) {
//                Class entityClass = entry.getKey();
//                IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>) entityClass, id);
//                resolveBiDirChildFromService(child, mappedBiDirParent);
//            }
//        }
    }

    @Override
    public void setResolvedIds(Object mappedDto, IdentifiableEntity serviceEntity, String... fieldsToCheck) {
        for (IdentifiableEntity child : relationalEntityManagerUtil.findAllBiDirChildren(serviceEntity,fieldsToCheck)) {
            relationalDtoManager.addBiDirChildId(child,mappedDto);
        }
//        for (IdentifiableEntity biDirChild : relationalEntityManagerUtil.findSingleBiDirChildren(serviceEntity)) {
//            relationalDtoManager.addBiDirChildId(biDirChild,mappedDto);
//        }
//        for (Collection<? extends IdentifiableEntity> childrenCollection : relationalEntityManagerUtil.findBiDirChildCollections(serviceEntity).values()) {
//            for (IdentifiableEntity biDirChild : childrenCollection) {
//                relationalDtoManager.addBiDirChildId(biDirChild,mappedDto);
//            }
//        }
    }

//    private void resolveBiDirChildFromService(IdentifiableEntity child, IdentifiableEntity mappedBiDirParent) {
//        try {
//            //set child of mapped parent
//            relationalEntityManagerUtil.linkBiDirChild(mappedBiDirParent, child);
//            //backreference gets set in BiDirParentListener
//        } catch (ClassCastException e) {
//            throw new IllegalArgumentException("Found Child " + child + " is not of Type BiDirChild");
//        }
//    }

}
