package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildDto;
import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParent;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParentEntity;
import io.github.vincemann.generic.crud.lib.service.finder.CrudServiceFinder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
/**
 * Resolves {@link io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentId} to corresponding {@link BiDirParentEntity}.
 * Adds mapped {@link BiDirChild} to {@link BiDirParent}'s children by calling {@link BiDirParent#addChild(BiDirChild)} -> sets Backreference
 */
public class BiDirChildIdResolver extends EntityIdResolver<BiDirChild, BiDirChildDto> {

    public BiDirChildIdResolver(CrudServiceFinder crudServiceFinder) {
        super(crudServiceFinder, BiDirChildDto.class);
    }

    public void resolveEntityIds(BiDirChild mappedBiDirChild, BiDirChildDto biDirChildDto) throws EntityMappingException {
        try {
            Map<Class, Serializable> allParentIdToClassMappings = biDirChildDto.findAllParentIds();
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
                mappedDto.addParentsId(biDirParent);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}
