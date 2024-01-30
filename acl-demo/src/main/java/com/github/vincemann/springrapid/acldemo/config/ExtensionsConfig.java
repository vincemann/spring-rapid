package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.service.ext.acl.RoleGainsPermissionAboutSavedAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.acldemo.service.ext.NeedRoleForSaveExtension;
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
    public RoleGainsPermissionAboutSavedAclExtension vetHasFullPermissionAboutSavedAclExtension(){
        return new RoleGainsPermissionAboutSavedAclExtension(MyRoles.VET, BasePermission.ADMINISTRATION);
    }

    @Bean
    public NeedRoleForSaveExtension onlyVetAndAdminCanCreateSecurityExtension(){
        return new NeedRoleForSaveExtension(MyRoles.VET);
    }

    @Bean
    public RoleGainsPermissionAboutSavedAclExtension vetsGainReadPermissionAboutSavedAclExtension(){
        return new RoleGainsPermissionAboutSavedAclExtension(MyRoles.VET,BasePermission.READ);
    }

    @ConditionalOnMissingBean(name = "needCreatePermissionOnParentForSaveExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @DefaultSecurityExtension
    @Bean
    public NeedCreatePermissionOnParentForSaveExtension needCreatePermissionOnParentForSaveExtension(){
        return new NeedCreatePermissionOnParentForSaveExtension();
    }
}