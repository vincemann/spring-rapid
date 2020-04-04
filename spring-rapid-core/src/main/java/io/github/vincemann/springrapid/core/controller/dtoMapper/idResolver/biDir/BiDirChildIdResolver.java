package io.github.vincemann.springrapid.core.controller.dtoMapper.idResolver.biDir;

import io.github.vincemann.springrapid.core.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.springrapid.core.dto.biDir.BiDirChildDto;
import io.github.vincemann.springrapid.core.model.biDir.child.BiDirChild;
import io.github.vincemann.springrapid.core.model.biDir.parent.BiDirParent;
import io.github.vincemann.springrapid.core.model.biDir.parent.BiDirParentEntity;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.core.dto.biDir.BiDirParentId;

import java.io.Serializable;
import java.util.Map;

/**
 * Resolves {@link BiDirParentId} to corresponding {@link BiDirParentEntity}.
 * Adds mapped {@link BiDirChild} to {@link BiDirParent}'s children by calling {@link BiDirParent#addChild(BiDirChild)} -> sets Backreference
 */
public class BiDirChildIdResolver extends EntityIdResolver<BiDirChild, BiDirChildDto> {

    public BiDirChildIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, BiDirChildDto.class);
    }

    public void resolveEntityIds(BiDirChild mappedBiDirChild, BiDirChildDto biDirChildDto) throws DtoMappingException {
        try {
            Map<Class, Serializable> allParentIdToClassMappings = biDirChildDto.findAllBiDirParentIds();
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
