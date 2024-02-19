package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.service.ext.acl.AuthenticatedGainsAdminPermissionOnCreatedAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.acl.CleanUpAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.acl.CreatedInheritsParentsPermissionsAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.acl.OwnerGainsAdminPermissionOnCreatedAclExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.CrudAclChecksExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForCreateExtension;
import com.github.vincemann.springrapid.core.DefaultExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Slf4j
public class RapidAclExtensionsAutoConfiguration {

    public RapidAclExtensionsAutoConfiguration() {

    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = "ownerGainsAdminPermissionOnCreatedAclExtension")
    public OwnerGainsAdminPermissionOnCreatedAclExtension ownerGainsAdminPermissionOnCreatedAclExtension(){
        return new OwnerGainsAdminPermissionOnCreatedAclExtension();
    }


    @ConditionalOnMissingBean(name = "crudAclChecksSecurityExtension")
    @DefaultExtension(qualifier = Secured.class, service = CrudService.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    @ConditionalOnProperty(name = "rapid-acl.default-acl-checks", havingValue = "true", matchIfMissing = true)
    public CrudAclChecksExtension crudAclChecksSecurityExtension(){
        return new CrudAclChecksExtension();
    }

    @ConditionalOnMissingBean(name = "needCreatePermissionOnParentForCreateExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public NeedCreatePermissionOnParentForCreateExtension needCreatePermissionOnParentForCreateExtension(){
        return new NeedCreatePermissionOnParentForCreateExtension();
    }

    @ConditionalOnMissingBean(name = "authenticatedGainsAdminPermissionOnCreatedAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public AuthenticatedGainsAdminPermissionOnCreatedAclExtension authenticatedGainsAdminPermissionOnCreatedAclExtension(){
        return new AuthenticatedGainsAdminPermissionOnCreatedAclExtension();
    }

//    @ConditionalOnMissingBean(name = "adminGainsAdminPermissionAboutSavedAclExtension")
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    @DefaultAclExtension
//    @Bean
////    @ConditionalOnProperty(name = "rapid-acl.adminFullAccess", havingValue = "true", matchIfMissing = true)
//    @ConditionalOnCustomProperties(properties = {"rapid-acl.defaultAclExtensions", "rapid-acl.adminFullAccess"})
//    public AdminGainsAdminPermissionAboutSavedAclExtension adminGainsAdminPermissionAboutSavedAclExtension(){
//        return new AdminGainsAdminPermissionAboutSavedAclExtension();
//    }

    @Bean
    @ConditionalOnMissingBean(name = "savedInheritsParentsPermissionsAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CreatedInheritsParentsPermissionsAclExtension savedInheritsParentsPermissionsAclExtension(){
        return new CreatedInheritsParentsPermissionsAclExtension();
    }

    @Bean
    @ConditionalOnMissingBean(name = "cleanUpAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @DefaultExtension(qualifier = Acl.class, service = CrudService.class)
    @ConditionalOnProperty(name = "rapid-acl.cleanup-acl", havingValue = "true", matchIfMissing = true)
    public CleanUpAclExtension cleanUpAclExtension(){
        return new CleanUpAclExtension();
    }
}
