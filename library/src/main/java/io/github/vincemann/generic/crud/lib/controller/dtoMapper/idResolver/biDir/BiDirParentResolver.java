package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentDto;
import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.finder.CrudServiceFinder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

@Component
public class BiDirParentResolver extends EntityIdResolver<BiDirParent, BiDirParentDto> {

    public BiDirParentResolver(CrudServiceFinder crudServiceFinder) {
        super(crudServiceFinder, BiDirParentDto.class);
    }

    public void resolveServiceEntityIds(BiDirParent mappedBiDirParent, BiDirParentDto biDirParentDto) throws EntityMappingException {
        try {
            //find and handle single Children
            Map<Class, Serializable> allChildIdToClassMappings = biDirParentDto.findChildrenIds();
            for (Map.Entry<Class, Serializable> childIdToClassMapping : allChildIdToClassMappings.entrySet()) {
                Object child = findEntityFromService(childIdToClassMapping);
                resolveBiDirChildFromService(child,mappedBiDirParent);
            }
            //find and handle children collections
            Map<Class, Collection<Serializable>> allChildrenIdCollection = biDirParentDto.findChildrenIdCollections();
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
                mappedDto.addChildsId(biDirChild);
            }
            for(Collection<? extends BiDirChild> childrenCollection: serviceEntity.getChildrenCollections().keySet()){
                for(BiDirChild biDirChild: childrenCollection){
                    mappedDto.addChildsId(biDirChild);
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
