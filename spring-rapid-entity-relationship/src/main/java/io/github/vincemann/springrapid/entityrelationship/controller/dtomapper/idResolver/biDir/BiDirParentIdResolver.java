package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir;

import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.EntityIdResolver;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

public class BiDirParentIdResolver extends EntityIdResolver<BiDirParent, BiDirParentDto> {

    public BiDirParentIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, BiDirParentDto.class);
    }

    public void resolveEntityIds(BiDirParent mappedBiDirParent, BiDirParentDto biDirParentDto) throws DtoMappingException {
        try {
            //find and handle single Children
            Map<Class, Serializable> allChildIdToClassMappings = biDirParentDto.findBiDirChildrenIds();
            for (Map.Entry<Class, Serializable> childIdToClassMapping : allChildIdToClassMappings.entrySet()) {
                Object child = findEntityFromService(childIdToClassMapping);
                resolveBiDirChildFromService(child,mappedBiDirParent);
            }
            //find and handle children collections
            Map<Class, Collection<Serializable>> allChildrenIdCollection = biDirParentDto.findBiDirChildrenIdCollections();
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
            for(BiDirChild biDirChild: serviceEntity.getChildren()){
                mappedDto.addBiDirChildsId(biDirChild);
            }
            for(Collection<? extends BiDirChild> childrenCollection: serviceEntity.getChildrenCollections().keySet()){
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
            mappedBiDirParent.addChild(biDirChild);
            //backreference gets set in BiDirParentListener
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Found Child " + child + " is not of Type BiDirChild");
        }
    }

}
