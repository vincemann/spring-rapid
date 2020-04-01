package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir;

import io.github.vincemann.generic.crud.lib.advice.log.LogInteraction;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirParentDto;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.uniDir.parent.UniDirParent;
import io.github.vincemann.generic.crud.lib.service.locator.CrudServiceLocator;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

public class UniDirParentIdResolver extends EntityIdResolver<UniDirParent, UniDirParentDto> {

    public UniDirParentIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, UniDirParentDto.class);
    }

    public void resolveEntityIds(UniDirParent mappedUniDirParent, UniDirParentDto uniDirParentDto) throws DtoMappingException {
        try {
            //find and handle single Children
            Map<Class, Serializable> allChildIdToClassMappings = uniDirParentDto.findUniDirChildrenIds();
            for (Map.Entry<Class, Serializable> childIdToClassMapping : allChildIdToClassMappings.entrySet()) {
                Object child = findEntityFromService(childIdToClassMapping);
                mappedUniDirParent._addChild(child);
            }
            //find and handle children collections
            Map<Class, Collection<Serializable>> allChildrenIdCollection = uniDirParentDto.findUniDirChildrenIdCollections();
            for (Map.Entry<Class, Collection<Serializable>> entry : allChildrenIdCollection.entrySet()) {
                Collection<Serializable> idCollection = entry.getValue();
                for (Serializable id : idCollection) {
                    Object child = findEntityFromService(new AbstractMap.SimpleEntry<>(entry.getKey(), id));
                    mappedUniDirParent._addChild(child);
                }
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resolveDtoIds(UniDirParentDto mappedDto, UniDirParent serviceEntity){
        try {
            for (Object child : serviceEntity._getChildren()) {
                mappedDto.addUniDirChildsId((IdentifiableEntity)child);
            }
            for (Collection childrenCollection : serviceEntity._getChildrenCollections().keySet()) {
                for (Object child : childrenCollection) {
                    mappedDto.addUniDirChildsId((IdentifiableEntity) child);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
