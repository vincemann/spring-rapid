package com.github.vincemann.springrapid.entityrelationship.controller.dtomapper;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.biDir.BiDirChildIdResolver;
import com.github.vincemann.springrapid.entityrelationship.dto.child.UniDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.model.child.UniDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;

import java.io.Serializable;
import java.util.Map;

/**
 * Same as {@link BiDirChildIdResolver} but without backref setting and for {@link UniDirChild}.
 *
 * @see EntityIdResolver
 */
public class UniDirChildIdResolver extends EntityIdResolver<UniDirChild, UniDirChildDto> {

    public UniDirChildIdResolver(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator, UniDirChildDto.class);
    }

    public void resolveEntityIds(UniDirChild mappedUniDirChild, UniDirChildDto uniDirChildDto) throws BadEntityException, EntityNotFoundException {
        Map<Class<UniDirParent>, Serializable> parentTypeIdMappings = uniDirChildDto.findUniDirParentIds();
        for (Map.Entry<Class<UniDirParent>, Serializable> entry : parentTypeIdMappings.entrySet()) {
            Class entityClass = entry.getKey();
            UniDirParent parent = findEntityFromService((Class<IdentifiableEntity>) entityClass, entry.getValue());
            try {
                //set parent of mapped child
                mappedUniDirChild.linkUniDirParent(parent);
                //dont set backref, because Parent does not know about Child (uniDir)
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Found Parent " + parent + " is not of Type UniDirParent");
            }
        }
    }

    @Override
    public void resolveDtoIds(UniDirChildDto mappedDto, UniDirChild serviceEntity) {
        for (UniDirParent uniDirParent : serviceEntity.findSingleUniDirParents()) {
            mappedDto.addUniDirParentId(uniDirParent);
        }
    }
}
