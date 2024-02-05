package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.service.ext.acl.RoleGainsPermissionOnCreatedAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.acldemo.service.ext.sec.NeedRoleForCreateExtension;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;

@Configuration
public class ExtensionsConfig {

    @Bean
    public RoleGainsPermissionOnCreatedAclExtension vetGainsAdminPermissionOnCreated(){
        return new RoleGainsPermissionOnCreatedAclExtension(MyRoles.VET, BasePermission.ADMINISTRATION);
    }

    @Bean
    public NeedRoleForCreateExtension onlyVetAndAdminCanCreate(){
        return new NeedRoleForCreateExtension(MyRoles.VET);
    }

    @Bean
    public RoleGainsPermissionOnCreatedAclExtension vetsGainsReadPermissionOnCreated(){
        return new RoleGainsPermissionOnCreatedAclExtension(MyRoles.VET,BasePermission.READ);
    }

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @DefaultSecurityExtension
    @Bean
    public NeedCreatePermissionOnParentForSaveExtension needCreatePermissionOnParentForCreate(){
        return new NeedCreatePermissionOnParentForSaveExtension();
    }
}
