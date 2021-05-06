package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.service.extensions.acl.RoleHasPermissionAboutSavedAclExtension;
import com.github.vincemann.springrapid.acl.service.extensions.security.OnlyRoleCanSaveSecurityExtension;
import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.security.acls.domain.BasePermission;

@ServiceConfig
public class ServiceExtensionsConfig {

    @Bean
    public RoleHasPermissionAboutSavedAclExtension vetHasFullPermissionAboutSavedAclExtension(){
        return new RoleHasPermissionAboutSavedAclExtension(MyRoles.VET, BasePermission.ADMINISTRATION);
    }

    @Bean
    public OnlyRoleCanSaveSecurityExtension onlyVetCanCreateSecurityExtension(){
        return new OnlyRoleCanSaveSecurityExtension(MyRoles.VET);
    }

    @Bean
    public RoleHasPermissionAboutSavedAclExtension vetsHaveReadPermission(){
        return new RoleHasPermissionAboutSavedAclExtension(MyRoles.VET,BasePermission.READ);
    }
}
