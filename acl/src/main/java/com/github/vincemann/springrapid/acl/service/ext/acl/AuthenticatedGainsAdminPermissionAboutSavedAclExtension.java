package com.github.vincemann.springrapid.acl.service.ext.acl;

import org.springframework.stereotype.Component;
import org.springframework.security.acls.domain.BasePermission;

@Component
public class AuthenticatedGainsAdminPermissionAboutSavedAclExtension extends AuthenticatedGainsPermissionAboutSavedAclExtension {

    public AuthenticatedGainsAdminPermissionAboutSavedAclExtension() {
        super(BasePermission.ADMINISTRATION);
    }
}