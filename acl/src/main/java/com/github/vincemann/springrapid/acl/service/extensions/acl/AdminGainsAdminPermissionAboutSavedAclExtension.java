package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.security.acls.domain.BasePermission;

// note that admin is allowed everything when AdminGlobalSecurityRule is active
@ServiceComponent
public class AdminGainsAdminPermissionAboutSavedAclExtension extends RoleGainsPermissionAboutSavedAclExtension {
    public AdminGainsAdminPermissionAboutSavedAclExtension() {
        super(Roles.ADMIN, BasePermission.ADMINISTRATION);
    }


}
