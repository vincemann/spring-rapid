package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.springrapid.core.sec.Roles;
import org.springframework.stereotype.Component;
import org.springframework.security.acls.domain.BasePermission;

// note that admin is allowed everything when AdminGlobalSecurityRule is active
@Component
public class AdminGainsAdminPermissionAboutSavedAclExtension extends RoleGainsPermissionAboutSavedAclExtension {
    public AdminGainsAdminPermissionAboutSavedAclExtension() {
        super(Roles.ADMIN, BasePermission.ADMINISTRATION);
    }


}
