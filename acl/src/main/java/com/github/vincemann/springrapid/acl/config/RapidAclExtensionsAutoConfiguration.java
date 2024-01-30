package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.DefaultAclExtension;
import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.service.ext.sec.CrudAclChecksSecurityExtension;
import com.github.vincemann.springrapid.acl.service.ext.acl.*;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.core.config.RapidJsonAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.core.util.condition.ConditionalOnCustomProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@Configuration
@Slf4j
@AutoConfigureAfter(RapidJsonAutoConfiguration.class)
public class RapidAclExtensionsAutoConfiguration {

    public RapidAclExtensionsAutoConfiguration() {

    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = "ownerGainsAdminPermissionAboutSavedAclExtension")
    public OwnerGainsAdminPermissionAboutSavedAclExtension ownerGainsAdminPermissionAboutSavedAclExtension(){
        return new OwnerGainsAdminPermissionAboutSavedAclExtension();
    }

    @ConditionalOnMissingBean(name = "crudAclChecksSecurityExtension")
    @DefaultSecurityExtension
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
//    @ConditionalOnProperty(name = "rapid-acl.defaultAclChecks", havingValue = "true", matchIfMissing = true)
    @ConditionalOnCustomProperties(properties = {"rapid-acl.defaultSecurityExtensions", "rapid-acl.defaultAclChecks"})
    public CrudAclChecksSecurityExtension crudAclChecksSecurityExtension(){
        return new CrudAclChecksSecurityExtension();
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
    public AuthenticatedGainsAdminPermissionAboutSavedAclExtension authenticatedGainsAdminPermissionAboutSavedAclExtension(){
        return new AuthenticatedGainsAdminPermissionAboutSavedAclExtension();
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
    public SavedInheritsParentsPermissionsAclExtension savedInheritsParentsPermissionsAclExtension(){
        return new SavedInheritsParentsPermissionsAclExtension();
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
