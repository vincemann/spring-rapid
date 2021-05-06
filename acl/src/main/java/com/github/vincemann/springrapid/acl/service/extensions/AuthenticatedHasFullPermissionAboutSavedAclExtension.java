package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Optional;

@ServiceComponent
public class AuthenticatedHasFullPermissionAboutSavedAclExtension extends AuthenticatedHasPermissionAboutSavedAclExtension {

    public AuthenticatedHasFullPermissionAboutSavedAclExtension() {
        super(BasePermission.ADMINISTRATION);
    }
}
