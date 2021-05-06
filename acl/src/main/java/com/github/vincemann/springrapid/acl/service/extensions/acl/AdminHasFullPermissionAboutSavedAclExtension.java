package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.security.acls.domain.BasePermission;

@ServiceComponent
public class AdminHasFullPermissionAboutSavedAclExtension extends RoleHasPermissionAboutSavedAclExtension {
    public AdminHasFullPermissionAboutSavedAclExtension() {
        super(Roles.ADMIN, BasePermission.ADMINISTRATION);
    }


}
