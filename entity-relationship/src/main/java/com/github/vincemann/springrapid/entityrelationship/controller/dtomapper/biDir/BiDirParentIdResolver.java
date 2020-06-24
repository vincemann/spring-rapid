package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir;


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
import java.util.AbstractMap;
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

    public void resolveEntityIds(BiDirParent mappedBiDirParent, BiDirParentDto biDirParentDto) throws BadEntityException, EntityNotFoundException {
        try {
            //find and handle single Children
            Map<Class, Serializable> allChildIdToClassMappings = biDirParentDto.findAllBiDirChildIds();
            for (Map.Entry<Class, Serializable> childIdToClassMapping : allChildIdToClassMappings.entrySet()) {
                Object child = findEntityFromService(childIdToClassMapping);
                resolveBiDirChildFromService(child,mappedBiDirParent);
            }
            //find and handle children collections
            Map<Class, Collection<Serializable>> allChildrenIdCollection = biDirParentDto.findAllBiDirChildIdCollections();
            for (Map.Entry<Class, Collection<Serializable>> entry: allChildrenIdCollection.entrySet()){
                Collection<Serializable> idCollection = entry.getValue();
                for(Serializable id: idCollection){
                    Object child = findEntityFromService(new AbstractMap.SimpleEntry<>(entry.getKey(),id));
                    resolveBiDirChildFromService(child,mappedBiDirParent);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resolveDtoIds(BiDirParentDto mappedDto, BiDirParent serviceEntity) {
        try {
            for(BiDirChild biDirChild: serviceEntity.findBiDirChildren()){
                mappedDto.addBiDirChildsId(biDirChild);
            }
            for(Collection<? extends BiDirChild> childrenCollection: serviceEntity.findAllBiDirChildCollections().keySet()){
                for(BiDirChild biDirChild: childrenCollection){
                    mappedDto.addBiDirChildsId(biDirChild);
                }
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    private void resolveBiDirChildFromService(Object child, BiDirParent mappedBiDirParent) throws IllegalAccessException {
        try {
            BiDirChild biDirChild = ((BiDirChild) child);
            //set child of mapped parent
            mappedBiDirParent.addBiDirChild(biDirChild);
            //backreference gets set in BiDirParentListener
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Found Child " + child + " is not of Type BiDirChild");
        }
    }

}
