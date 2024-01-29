package com.github.vincemann.springrapid.acldemo.service.ext;

import com.github.vincemann.springrapid.acl.service.ext.sec.AbstractSecurityExtension;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.acls.domain.BasePermission;

@Component
public class NeedCreatePermissionForSubscribingSecurityExtension extends AbstractSecurityExtension<VisitService>
        implements GenericCrudServiceExtension<VisitService, Visit,Long>, VisitService
{
    @Override
    public void subscribeOwner(Long ownerId, Long visitId) throws EntityNotFoundException {
        getSecurityChecker().checkPermission(visitId,Visit.class, BasePermission.CREATE);
        getNext().subscribeOwner(ownerId,visitId);
    }

    @Override
    public void unsubscribeOwner(Long ownerId, Long visitId) throws BadEntityException, EntityNotFoundException {
        getSecurityChecker().checkPermission(visitId,Visit.class, BasePermission.CREATE);
        getNext().unsubscribeOwner(ownerId,visitId);
    }
}
