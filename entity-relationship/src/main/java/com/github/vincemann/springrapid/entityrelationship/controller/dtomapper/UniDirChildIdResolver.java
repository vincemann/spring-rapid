package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper;


import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.BiDirChildIdResolver;
import com.github.vincemann.springrapid.entityrelationship.dto.child.UniDirChildDto;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.model.UniDirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.UniDirChild;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;

import java.io.Serializable;
import java.util.Map;

/**
 * Same as {@link BiDirChildIdResolver} but without backref setting and for
 * {@link UniDirEntity}s.
 *
 * @see EntityIdResolver
 */
public class UniDirChildIdResolver extends EntityIdResolver<UniDirChild, UniDirChildDto> {

    public UniDirChildIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, UniDirChildDto.class);
    }

    public void resolveEntityIds(UniDirChild mappedUniDirChild, UniDirChildDto uniDirChildDto) throws BadEntityException, EntityNotFoundException {
        try {
            Map<Class, Serializable> allParentIdToClassMappings = uniDirChildDto.findAllUniDirParentIds();
            for (Map.Entry<Class, Serializable> parentIdToClassMapping : allParentIdToClassMappings.entrySet()) {
                Object parent = findEntityFromService(parentIdToClassMapping);
                try {
                    //set parent of mapped child
                    mappedUniDirChild.addUniDirParent(parent);
                    //dont set backref, because Parent does not know about Child (uniDir)
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Found Parent " + parent + " is not of Type UniDirParent");
                }
            }
        } catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resolveDtoIds(UniDirChildDto mappedDto, UniDirChild serviceEntity ){
        try {
            for (Object uniDirParent : serviceEntity.findUniDirParents()) {
                mappedDto.addUniDirParentsId((IdentifiableEntity) uniDirParent);
            }
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}
