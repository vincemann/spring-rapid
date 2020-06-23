package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir;


import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.EntityIdResolver;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParentEntity;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentId;

import java.io.Serializable;
import java.util.Map;

/**
 * Used by {@link IdResolvingDtoPostProcessor}.
 * Resolves {@link BiDirParentId} to corresponding {@link BiDirParentEntity}.
 * Adds mapped {@link BiDirChild} to {@link BiDirParent#getChildren()}'s  -> sets Backreference
 *
 * @see EntityIdResolver
 */
public class BiDirChildIdResolver extends EntityIdResolver<BiDirChild, BiDirChildDto> {

    public BiDirChildIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, BiDirChildDto.class);
    }

    public void resolveEntityIds(BiDirChild mappedBiDirChild, BiDirChildDto biDirChildDto) throws BadEntityException, EntityNotFoundException {
        try {
            Map<Class, Serializable> allParentIdToClassMappings = biDirChildDto.findTypeBiDirParentIdMap();
            for (Map.Entry<Class, Serializable> parentIdToClassMapping : allParentIdToClassMappings.entrySet()) {
                Object parent = findEntityFromService(parentIdToClassMapping);
                try {
                    BiDirParent biDirParent = ((BiDirParent) parent);
                    //set parent of mapped child
                    mappedBiDirChild.setParentRef(biDirParent);
                    //backreference gets set in BiDirChildListener
                }catch (ClassCastException e){
                    throw new IllegalArgumentException("Found Parent " + parent + " is not of Type BiDirParent");
                }
            }
        } catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resolveDtoIds(BiDirChildDto mappedDto, BiDirChild serviceEntity) {
        try {
            for(BiDirParent biDirParent: serviceEntity.findParents()){
                mappedDto.addBiDirParentsId(biDirParent);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}
