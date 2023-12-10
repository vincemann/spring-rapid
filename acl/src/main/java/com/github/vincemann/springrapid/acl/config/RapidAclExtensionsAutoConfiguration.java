package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.DefaultAclExtension;
import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.service.extensions.security.CrudAclChecksSecurityExtension;
import com.github.vincemann.springrapid.acl.service.extensions.acl.*;
import com.github.vincemann.springrapid.acl.service.extensions.security.NeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.core.config.RapidJsonAutoConfiguration;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@ServiceConfig
@Slf4j
@AutoConfigureAfter(RapidJsonAutoConfiguration.class)
public class RapidAclExtensionsAutoConfiguration {

    public RapidAclExtensionsAutoConfiguration() {

    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean(name = "ownerHasFullPermissionAboutSavedAclExtension")
    public OwnerHasFullPermissionAboutSavedAclExtension ownerHasFullPermissionAboutSavedAclExtension(){
        return new OwnerHasFullPermissionAboutSavedAclExtension();
    }

    @ConditionalOnMissingBean(name = "crudAclChecksSecurityExtension")
    @Qualifier("crudAclChecksSecurityExtension")
//    @DefaultSecurityExtension
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public CrudAclChecksSecurityExtension crudAclChecksSecurityExtension(){
        return new CrudAclChecksSecurityExtension();
    }

    @ConditionalOnMissingBean(name = "needCreatePermissionOnParentForSaveExtension")
    @Qualifier("needCreatePermissionOnParentForSaveExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    @DefaultSecurityExtension
    @Bean
    public NeedCreatePermissionOnParentForSaveExtension needCreatePermissionOnParentForSaveExtension(){
        return new NeedCreatePermissionOnParentForSaveExtension();
    }

    @ConditionalOnMissingBean(name = "authenticatedHasFullPermissionAboutSavedAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public AuthenticatedHasFullPermissionAboutSavedAclExtension authenticatedHasFullPermissionAboutSavedAclExtension(){
        return new AuthenticatedHasFullPermissionAboutSavedAclExtension();
    }

    @ConditionalOnMissingBean(name = "adminHasFullPermissionAboutSavedAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    @DefaultAclExtension
    @Bean
    public AdminHasFullPermissionAboutSavedAclExtension adminHasFullPermissionAboutSavedAclExtension(){
        return new AdminHasFullPermissionAboutSavedAclExtension();
    }

    @Bean
    @ConditionalOnMissingBean(name = "savedInheritsParentsAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SavedInheritsParentsAclExtension savedInheritsParentsAclExtension(){
        return new SavedInheritsParentsAclExtension();
    }

    @Bean
    @ConditionalOnMissingBean(name = "cleanUpAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    @DefaultAclExtension
    public CleanUpAclExtension cleanUpAclExtension(){
        return new CleanUpAclExtension();
    }
}
