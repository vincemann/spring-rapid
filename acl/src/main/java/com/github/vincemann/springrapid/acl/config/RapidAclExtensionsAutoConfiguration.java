package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.DefaultAclExtension;
import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.CrudAclChecksExtension;
import com.github.vincemann.springrapid.acl.service.ext.acl.*;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForSaveExtension;
import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.core.util.condition.ConditionalOnCustomProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@Configuration
@Slf4j
public class RapidAclExtensionsAutoConfiguration {

    public RapidAclExtensionsAutoConfiguration() {

    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = "ownerGainsAdminPermissionAboutSavedAclExtension")
    public OwnerGainsAdminPermissionOnCreatedAclExtension ownerGainsAdminPermissionAboutSavedAclExtension(){
        return new OwnerGainsAdminPermissionOnCreatedAclExtension();
    }

    @ConditionalOnMissingBean(name = "crudAclChecksSecurityExtension")
    @DefaultSecurityExtension
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
//    @ConditionalOnProperty(name = "rapid-acl.defaultAclChecks", havingValue = "true", matchIfMissing = true)
    @ConditionalOnCustomProperties(properties = {"rapid-acl.defaultSecurityExtensions", "rapid-acl.defaultAclChecks"})
    public CrudAclChecksExtension crudAclChecksSecurityExtension(){
        return new CrudAclChecksExtension();
    }

    @ConditionalOnMissingBean(name = "needCreatePermissionOnParentForSaveExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    @DefaultSecurityExtension
    @Bean
    public NeedCreatePermissionOnParentForSaveExtension needCreatePermissionOnParentForSaveExtension(){
        return new NeedCreatePermissionOnParentForSaveExtension();
    }

    @ConditionalOnMissingBean(name = "authenticatedGainsAdminPermissionAboutSavedAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public AuthenticatedGainsAdminPermissionOnCreatedAclExtension authenticatedGainsAdminPermissionAboutSavedAclExtension(){
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
    @DefaultAclExtension
//    @ConditionalOnProperty(name = "rapid-acl.cleanupAcl", havingValue = "true", matchIfMissing = true)
    @ConditionalOnCustomProperties(properties = {"rapid-acl.defaultAclExtensions", "rapid-acl.cleanupAcl"})
    public CleanUpAclExtension cleanUpAclExtension(){
        return new CleanUpAclExtension();
    }
}
