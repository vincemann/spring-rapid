package io.github.vincemann.springrapid.acl.plugin;

import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

@Slf4j
public class AuthenticatedFullAccessAclPlugin
        extends AbstractAclPlugin {


    public AuthenticatedFullAccessAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService) {
        super(permissionService, mutableAclService);
    }

    @CalledByProxy
    public void onAfterSave(IdentifiableEntity<Serializable> requestEntity, IdentifiableEntity<Serializable> returnedEntity) {
        savePermissionForAuthenticatedOver(returnedEntity, BasePermission.ADMINISTRATION);
    }

}
