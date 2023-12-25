package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.service.extensions.acl.RoleGainsPermissionAboutSavedAclExtension;
import com.github.vincemann.springrapid.acl.service.extensions.security.NeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.acldemo.service.extensions.NeedRoleForSaveExtension;
import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;

@ServiceConfig
public class ServiceExtensionsConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RoleGainsPermissionAboutSavedAclExtension vetHasFullPermissionAboutSavedAclExtension(){
        return new RoleGainsPermissionAboutSavedAclExtension(MyRoles.VET, BasePermission.ADMINISTRATION);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public NeedRoleForSaveExtension onlyVetAndAdminCanCreateSecurityExtension(){
        return new NeedRoleForSaveExtension(MyRoles.VET);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
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
