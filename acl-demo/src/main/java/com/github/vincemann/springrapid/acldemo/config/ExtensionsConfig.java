package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.service.ext.acl.RoleGainsPermissionAboutSavedAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.acldemo.service.ext.sec.NeedRoleForSaveExtension;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;

@Configuration
public class ExtensionsConfig {

    @Bean
    public RoleGainsPermissionAboutSavedAclExtension vetGainsAdminPermissionForCreated(){
        return new RoleGainsPermissionAboutSavedAclExtension(MyRoles.VET, BasePermission.ADMINISTRATION);
    }

    @Bean
    public NeedRoleForSaveExtension onlyVetAndAdminCanCreate(){
        return new NeedRoleForSaveExtension(MyRoles.VET);
    }

    @Bean
    public RoleGainsPermissionAboutSavedAclExtension vetsGainReadPermissionForCreated(){
        return new RoleGainsPermissionAboutSavedAclExtension(MyRoles.VET,BasePermission.READ);
    }

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @DefaultSecurityExtension
    @Bean
    public NeedCreatePermissionOnParentForSaveExtension needCreatePermissionOnParentForCreate(){
        return new NeedCreatePermissionOnParentForSaveExtension();
    }
}
