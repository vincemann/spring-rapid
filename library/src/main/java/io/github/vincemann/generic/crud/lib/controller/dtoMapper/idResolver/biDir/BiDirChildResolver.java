package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirDtoChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.CrudServiceFinder;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
/**
 * Resolves {@link io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentId} to corresponding {@link io.github.vincemann.generic.crud.lib.model.biDir.BiDirParentEntity}.
 * Adds mapped {@link BiDirChild} to {@link BiDirParent}'s children by calling {@link BiDirParent#addChild(BiDirChild)} -> sets Backreference
 */
public class BiDirChildResolver extends EntityIdResolver<BiDirChild,BiDirDtoChild> {

    public BiDirChildResolver(CrudServiceFinder crudServiceFinder) {
        super(crudServiceFinder,BiDirDtoChild.class);
    }

    public void resolveServiceEntityIds(BiDirChild mappedBiDirChild, BiDirDtoChild biDirDtoChild) throws EntityMappingException {
        try {
            Map<Class, Serializable> allParentIdToClassMappings = biDirDtoChild.findAllParentIds();
            for (Map.Entry<Class, Serializable> parentIdToClassMapping : allParentIdToClassMappings.entrySet()) {
                Object parent = findEntityFromService(parentIdToClassMapping);
                try {
                    BiDirParent biDirParent = ((BiDirParent) parent);
                    //set parent of mapped child
                    mappedBiDirChild.findAndSetParent(biDirParent);
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
    public void resolveDtoIds(BiDirDtoChild mappedDto, BiDirChild serviceEntity) {
        try {
            for(BiDirParent biDirParent: serviceEntity.findParents()){
                mappedDto.addParentsId(biDirParent);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}
