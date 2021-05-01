package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.proxy.CrudAclChecksSecurityExtension;
import com.github.vincemann.springrapid.acl.service.extensions.AdminFullAccessAboutSavedAclExtension;
import com.github.vincemann.springrapid.acl.service.extensions.AuthenticatedFullAccessAboutSavedAclExtension;
import com.github.vincemann.springrapid.acl.service.extensions.CleanUpAclExtension;
import com.github.vincemann.springrapid.acl.service.extensions.SavedInheritsFromParentAclExtension;
import com.github.vincemann.springrapid.core.config.RapidJsonAutoConfiguration;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
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

    @ConditionalOnMissingBean(name = "crudAclChecksSecurityExtension")
    @Qualifier("crudAclChecksSecurityExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public CrudAclChecksSecurityExtension crudAclChecksSecurityExtension(){
        return new CrudAclChecksSecurityExtension();
    }

    @ConditionalOnMissingBean(AuthenticatedFullAccessAboutSavedAclExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public AuthenticatedFullAccessAboutSavedAclExtension authenticatedFullAccessAclExtension(){
        return new AuthenticatedFullAccessAboutSavedAclExtension();
    }

    @ConditionalOnMissingBean(AdminFullAccessAboutSavedAclExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public AdminFullAccessAboutSavedAclExtension adminFullAccessAclExtension(){
        return new AdminFullAccessAboutSavedAclExtension();
    }

    @Bean
    @ConditionalOnMissingBean(SavedInheritsFromParentAclExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SavedInheritsFromParentAclExtension inheritParentAclExtension(){
        return new SavedInheritsFromParentAclExtension();
    }

    @Bean
    @ConditionalOnMissingBean(CleanUpAclExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CleanUpAclExtension cleanUpAclExtension(){
        return new CleanUpAclExtension();
    }
}
