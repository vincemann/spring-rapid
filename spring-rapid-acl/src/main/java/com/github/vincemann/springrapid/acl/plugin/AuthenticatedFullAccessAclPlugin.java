package com.github.vincemann.springrapid.acl.plugin;

import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CalledByProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;

import java.io.Serializable;

@Slf4j
public class AuthenticatedFullAccessAclPlugin
        extends AbstractAclPlugin {


    public AuthenticatedFullAccessAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService) {
        super(permissionService, mutableAclService, mockAuthService);
    }

    @CalledByProxy
    public void onAfterSave(IdentifiableEntity<Serializable> requestEntity, IdentifiableEntity<Serializable> returnedEntity) {
        savePermissionForAuthenticatedOver(returnedEntity, BasePermission.ADMINISTRATION);
    }

}
