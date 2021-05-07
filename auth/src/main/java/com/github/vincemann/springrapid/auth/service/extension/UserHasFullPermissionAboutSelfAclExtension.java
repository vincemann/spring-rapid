package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.domain.AuthenticatingEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Optional;

public class UserHasFullPermissionAboutSelfAclExtension
        <E extends IdentifiableEntity<Id> & AuthenticatingEntity<Id>,Id extends Serializable>
        extends UserHasPermissionAboutSelfAclExtension<E,Id> {
    public UserHasFullPermissionAboutSelfAclExtension() {
        super(BasePermission.ADMINISTRATION);
    }

}
