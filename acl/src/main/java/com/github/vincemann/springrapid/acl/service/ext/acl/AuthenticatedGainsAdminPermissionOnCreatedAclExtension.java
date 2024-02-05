package com.github.vincemann.springrapid.acl.service.ext.acl;

import org.springframework.security.acls.domain.BasePermission;

public class AuthenticatedGainsAdminPermissionOnCreatedAclExtension extends AuthenticatedGainsPermissionOnCreatedAclExtension {

    public AuthenticatedGainsAdminPermissionOnCreatedAclExtension() {
        super(BasePermission.ADMINISTRATION);
    }
}
