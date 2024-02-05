package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.model.AuthenticatingEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.acls.domain.BasePermission;

import java.io.Serializable;

public class UserGainsAdminPermissionOnCreated
        <E extends IdentifiableEntity<Id> & AuthenticatingEntity<Id>,Id extends Serializable>
        extends UserGainsPermissionAboutSelfAclExtension<E,Id> {
    public UserGainsAdminPermissionOnCreated() {
        super(BasePermission.ADMINISTRATION);
    }

}
