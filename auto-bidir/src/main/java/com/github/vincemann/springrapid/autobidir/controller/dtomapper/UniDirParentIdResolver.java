package com.github.vincemann.springrapid.autobidir.controller.dtomapper;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.BiDirParentIdResolver;



import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Same as {@link BiDirParentIdResolver} but without backref setting and for : UniDirParent
 *
 * @see EntityIdResolver
 */
public class UniDirParentIdResolver extends EntityIdResolver<UniDirParent, UniDirParentDto> {

    public UniDirParentIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, UniDirParentDto.class);
    }

    public void injectEntitiesFromDtoIds(UniDirParent mappedUniDirParent, UniDirParentDto uniDirParentDto) throws BadEntityException, EntityNotFoundException {
        //find and handle single Children
        Map<Class<IdentifiableEntity>, Serializable> childTypeIdMappings = uniDirParentDto.findUniDirChildIds();
        for (Map.Entry<Class<IdentifiableEntity>, Serializable> entry : childTypeIdMappings.entrySet()) {
            Class entityClass = entry.getKey();
            IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>) entityClass, entry.getValue());
            mappedUniDirParent.linkUniDirChild(child);
        }
        //find and handle children collections
        Map<Class<IdentifiableEntity>, Collection<Serializable>> childTypeIdCollectionMappings = uniDirParentDto.findUniDirChildIdCollections();
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entry : childTypeIdCollectionMappings.entrySet()) {
            Collection<Serializable> idCollection = entry.getValue();
            for (Serializable id : idCollection) {
                Class entityClass = entry.getKey();
                IdentifiableEntity child = findEntityFromService((Class<IdentifiableEntity>)entityClass, id);
                mappedUniDirParent.linkUniDirChild(child);
            }
        }
    }

    @Override
    public void injectDtoIdsFromEntity(UniDirParentDto mappedDto, UniDirParent serviceEntity) {
        for (IdentifiableEntity child : serviceEntity.findSingleUniDirChildren()) {
            mappedDto.addUniDirChildId(child);
        }
        for (Collection<IdentifiableEntity> childrenCollection : serviceEntity.findUniDirChildCollections().keySet()) {
            for (IdentifiableEntity child : childrenCollection) {
                mappedDto.addUniDirChildId(child);
            }
        }
    }
}
