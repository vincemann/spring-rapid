package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.security.acls.domain.BasePermission;

@ServiceComponent
public class OwnerHasFullPermissionAboutSavedAclExtension extends OwnerHasPermissionAboutSavedAclExtension {
    public OwnerHasFullPermissionAboutSavedAclExtension() {
        super(BasePermission.ADMINISTRATION);
    }


}
