package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.security.acls.domain.BasePermission;

@ServiceComponent
public class AuthenticatedHasFullPermissionAboutSavedAclExtension extends AuthenticatedHasPermissionAboutSavedAclExtension {

    public AuthenticatedHasFullPermissionAboutSavedAclExtension() {
        super(BasePermission.ADMINISTRATION);
    }
}
