package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.service.ext.acl.RoleGainsPermissionOnCreatedAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.service.ext.sec.MyNeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.acldemo.service.ext.sec.NeedRoleForCreateExtension;
import com.github.vincemann.springrapid.core.DefaultExtension;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;

@Configuration
public class ExtensionsConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RoleGainsPermissionOnCreatedAclExtension vetGainsAdminPermissionOnCreated(){
        return new RoleGainsPermissionOnCreatedAclExtension(MyRoles.VET, BasePermission.ADMINISTRATION);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public NeedRoleForCreateExtension onlyVetAndAdminCanCreate(){
        return new NeedRoleForCreateExtension(MyRoles.VET);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RoleGainsPermissionOnCreatedAclExtension vetsGainReadPermissionOnCreated(){
        return new RoleGainsPermissionOnCreatedAclExtension(MyRoles.VET,BasePermission.READ);
    }

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @DefaultExtension(qualifier = Secured.class)
    @Bean
    public NeedCreatePermissionOnParentForSaveExtension needCreatePermissionOnParentForCreate(){
        return new MyNeedCreatePermissionOnParentForSaveExtension();
    }
}
