package com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir;


import com.github.vincemann.springrapid.autobidir.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.EntityIdResolver;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.IdResolvingDtoPostProcessor;



import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentId;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType.BiDirChildDto;

/**
 * Used by {@link IdResolvingDtoPostProcessor}.
 * Resolves {@link BiDirParentId} to corresponding {@link BiDirParentEntity}.
 * Adds mapped BiDirChild to {@link RelationalEntityManagerUtil#findSingleBiDirChildren(IdentifiableEntity)}}'s  -> sets Backreference
 *
 * @see EntityIdResolver
 */
public class BiDirChildIdResolver extends EntityIdResolver {

    public BiDirChildIdResolver() {
        super(BiDirChildDto);
    }

    public void setResolvedEntities(IdentifiableEntity mappedBiDirChild, Object biDirChildDto) throws BadEntityException, EntityNotFoundException {

        //find all parents by id and map them to child
        // create method for finding all child - Id mappings, then use findEntityFromService method to resolve entity then call relationalEntityManagerUtil.link...Entity(mappedEntity,relatedEntity);
        Map<Class<IdentifiableEntity>, Collection<Serializable>> parentTypeIdCollectionMappings = relationalDtoManager.findAllBiDirParentIds(biDirChildDto);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : parentTypeIdCollectionMappings.entrySet()) {
            Collection<Serializable> idCollection = entry.getValue();
            for (Serializable id : idCollection) {
                Class entityClass = entry.getKey();
                IdentifiableEntity parent = findEntityFromService((Class<IdentifiableEntity>) entityClass, id);
//                resolveBiDirParentFromService(parent, mappedBiDirChild);
                relationalEntityManagerUtil.linkBiDirParent(mappedBiDirChild,parent);

            }
        }


//        Map<Class<IdentifiableEntity>, Serializable> parentTypeIdMappings = relationalDtoManager.findBiDirParentIds(biDirChildDto);
//        for (Map.Entry<Class<IdentifiableEntity>, Serializable> entry : parentTypeIdMappings.entrySet()) {
//            Class entityClass = entry.getKey();
//            IdentifiableEntity parent = findEntityFromService((Class<IdentifiableEntity>)entityClass, entry.getValue());
//            try {
////                BiDirParent biDirParent = ((BiDirParent) parent);
////                //set parent of mapped child
////                mappedBiDirChild.linkBiDirParent(biDirParent);
////                //backreference gets set in BiDirChildListener
//                resolveBiDirParentFromService(parent, mappedBiDirChild);
//            } catch (ClassCastException e) {
//                throw new IllegalArgumentException("Found Parent " + parent + " is not of Type BiDirParent");
//            }
//        }
//
//        //find and handle parent collections
//        Map<Class<IdentifiableEntity>, Collection<Serializable>> parentTypeIdCollectionMappings = relationalDtoManager.findBiDirParentIdCollections(biDirChildDto);
//        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : parentTypeIdCollectionMappings.entrySet()) {
//            Collection<Serializable> idCollection = entry.getValue();
//            for (Serializable id : idCollection) {
//                Class entityClass = entry.getKey();
//                IdentifiableEntity parent = findEntityFromService((Class<IdentifiableEntity>) entityClass, id);
//                resolveBiDirParentFromService(parent, mappedBiDirChild);
//            }
//        }
    }

//    private void resolveBiDirParentFromService(IdentifiableEntity parent, IdentifiableEntity mappedBiDirChild) {
//        try {
//            //set parent of mapped parent
//            relationalEntityManagerUtil.linkBiDirParent(mappedBiDirChild,parent);
//            //backreference gets set in BiDirParentListener
//        } catch (ClassCastException e) {
//            throw new IllegalArgumentException("Found Child " + parent + " is not of Type BiDirChild");
//        }
//    }

    @Override
    public void setResolvedIds(Object mappedDto, IdentifiableEntity serviceEntity) {
        for (IdentifiableEntity parent : relationalEntityManagerUtil.findAllBiDirParents(serviceEntity)) {
            relationalDtoManager.addBiDirParentId(parent,mappedDto);
        }

//        for (IdentifiableEntity biDirParent : relationalEntityManagerUtil.findSingleBiDirParents(serviceEntity)) {
//            relationalDtoManager.addBiDirParentId(biDirParent,mappedDto);
//        }
//        for (Collection<? extends IdentifiableEntity> parentCollection : relationalEntityManagerUtil.findBiDirParentCollections(serviceEntity).values()) {
//            for (IdentifiableEntity biDirParent : parentCollection) {
//                relationalDtoManager.addBiDirParentId(biDirParent,mappedDto);
//            }
//        }
    }
}
