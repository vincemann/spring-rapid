package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.service.AceNotFoundException;
import com.github.vincemann.springrapid.acl.service.AclNotFoundException;
import com.github.vincemann.springrapid.acl.service.AclPermissionService;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.core.proxy.annotation.CreateProxy;
import com.github.vincemann.springrapid.core.proxy.annotation.DefineProxy;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.repositories.VisitRepository;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;




@DefineProxy(name = "acl", extensions = {
        "authenticatedHasFullPermissionAboutSavedAclExtension",
        "vetsHaveReadPermissionAboutSavedAclExtension",
        "ownerCanReadHisVisitsAclExtension"
})
@DefineProxy(name = "secured", extensions = {
        "onlyVetAndAdminCanCreateSecurityExtension",
        "vetCanOnlySaveOwnVisitsSecurityExtension"
})
@CreateProxy(qualifiers = Acl.class,proxies = "acl")
@CreateProxy(qualifiers = Secured.class,proxies = {"acl","secured"})
@Primary
@Service
@ServiceComponent
public class JpaVisitService extends JPACrudService<Visit,Long, VisitRepository>
        implements VisitService, TargetClassAware {


    private AclPermissionService aclPermissionService;

    @Override
    public void subscribeOwner(Owner owner, Visit visit) {
        aclPermissionService.savePermissionForUserOverEntity(owner.getUser().getEmail(),visit, BasePermission.READ);
    }

    @Override
    public void unsubscribeOwner(Owner owner, Visit visit) throws BadEntityException {
        try {
            aclPermissionService.deletePermissionForUserOverEntity(owner.getUser().getEmail(),visit, BasePermission.READ);
        } catch (AclNotFoundException | AceNotFoundException e) {
            throw new BadEntityException(e);
        }
    }

    @Autowired
    public void injectAclPermissionService(AclPermissionService aclPermissionService) {
        this.aclPermissionService = aclPermissionService;
    }

    @Override
    public Class<?> getTargetClass() {
        return JpaVisitService.class;
    }
}
