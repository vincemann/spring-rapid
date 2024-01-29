package com.github.vincemann.springrapid.acldemo.service.ext;

import com.github.vincemann.springrapid.acl.service.extensions.security.AbstractSecurityExtension;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import org.springframework.security.acls.domain.BasePermission;

@Component
public class NeedCreatePermissionForSubscribingSecurityExtension extends AbstractSecurityExtension<VisitService>
        implements GenericCrudServiceExtension<VisitService, Visit,Long>, VisitService
{
    @Override
    public void subscribeOwner(Owner owner, Visit visit) {
        getSecurityChecker().checkPermission(visit, BasePermission.CREATE);
        getNext().subscribeOwner(owner,visit);
    }

    @Override
    public void unsubscribeOwner(Owner owner, Visit visit) throws BadEntityException {
        getSecurityChecker().checkPermission(visit, BasePermission.CREATE);
        getNext().unsubscribeOwner(owner,visit);
    }
}
