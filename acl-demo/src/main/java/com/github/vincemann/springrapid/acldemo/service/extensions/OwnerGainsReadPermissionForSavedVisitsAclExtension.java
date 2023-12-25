package com.github.vincemann.springrapid.acldemo.service.extensions;

import com.github.vincemann.springrapid.acl.service.extensions.acl.AbstractAclExtension;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;

@ServiceComponent
public class OwnerGainsReadPermissionForSavedVisitsAclExtension extends AbstractAclExtension<VisitService>
        implements GenericCrudServiceExtension<VisitService, Visit,Long>
{
    @Override
    public Visit save(Visit visit) throws BadEntityException {
        Visit savedVisit = getNext().save(visit);
        Owner owner = savedVisit.getOwner();
        try {
            VerifyEntity.isPresent(owner,"visit must have owner");
        } catch (EntityNotFoundException e) {
            throw new BadEntityException(e);
        }
        aclPermissionService.savePermissionForUserOverEntity(owner.getUser().getContactInformation(),
                visit, BasePermission.READ);
        return savedVisit;
    }
}
