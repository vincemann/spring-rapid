package com.github.vincemann.springrapid.acl.service.ext.acl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.security.acls.domain.BasePermission;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnerGainsAdminPermissionOnCreatedAclExtension extends OwnerGainsPermissionOnCreatedAclExtension {
    public OwnerGainsAdminPermissionOnCreatedAclExtension() {
        super(BasePermission.ADMINISTRATION);
    }


}
