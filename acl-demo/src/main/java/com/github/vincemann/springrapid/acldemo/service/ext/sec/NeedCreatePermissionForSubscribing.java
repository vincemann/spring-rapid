package com.github.vincemann.springrapid.acldemo.service.ext.sec;

import com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.security.acls.domain.BasePermission;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NeedCreatePermissionForSubscribing extends SecurityExtension<VisitService>
        implements GenericCrudServiceExtension<VisitService, Visit,Long>, VisitService
{
    @Override
    public void subscribeOwner(Long ownerId, Long visitId) throws EntityNotFoundException {
        getAclTemplate().checkPermission(visitId,Visit.class, BasePermission.CREATE);
        getNext().subscribeOwner(ownerId,visitId);
    }

    @Override
    public void unsubscribeOwner(Long ownerId, Long visitId) throws BadEntityException, EntityNotFoundException {
        getAclTemplate().checkPermission(visitId,Visit.class, BasePermission.CREATE);
        getNext().unsubscribeOwner(ownerId,visitId);
    }
}
