package com.github.vincemann.springrapid.acldemo.service.ext.acl;

import com.github.vincemann.springrapid.acl.service.ext.acl.AclExtension;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnerGainsReadPermissionOnCreated
        extends AclExtension<VisitService>
        implements GenericCrudServiceExtension<VisitService, Visit,Long>
{
    @Transactional
    @Override
    public Visit create(Visit visit) throws BadEntityException {
        Visit savedVisit = getNext().create(visit);
        Owner owner = savedVisit.getOwner();
        try {
            VerifyEntity.isPresent(owner,"visit must have owner");
        } catch (EntityNotFoundException e) {
            throw new BadEntityException(e);
        }
        rapidAclService.savePermissionForUserOverEntity(owner.getUser().getContactInformation(),
                visit, BasePermission.READ);
        return savedVisit;
    }
}
