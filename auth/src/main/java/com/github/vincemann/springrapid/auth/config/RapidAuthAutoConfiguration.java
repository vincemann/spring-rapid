package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.controller.owner.AuthOwnerLocator;
import com.github.vincemann.springrapid.auth.controller.owner.UserOwnerLocator;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.sec.*;
import com.github.vincemann.springrapid.auth.service.ctx.AuthContextService;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.config.RapidCoreAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import com.github.vincemann.springrapid.core.model.audit.LongIdAuthAuditorAwareImpl;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.ctx.ContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.AclService;

import java.util.List;

@Configuration
@Slf4j
@EnableConfigurationProperties
@AutoConfigureBefore({RapidCoreAutoConfiguration.class,RapidAclAutoConfiguration.class}) // permission evaluator is overridden this way
public class RapidAuthAutoConfiguration {

    @ConfigurationProperties(prefix="rapid-auth")
    @ConditionalOnMissingBean(AuthProperties.class)
    @Bean
    public AuthProperties authProperties(CoreProperties coreProperties) {
        return new AuthProperties(coreProperties);
    }


    @Bean
    @ConditionalOnMissingBean(ContextService.class)
    public ContextService contextService(){
        return new AuthContextService();
    }

    // for finding owner of entities
    @Bean
    @ConditionalOnMissingBean(name = "ownerLocator")
    public OwnerLocator<AuditingEntity> ownerLocator(){
        return new AuthOwnerLocator();
    }

    // for finding owner of users
    @Bean
    @ConditionalOnMissingBean(name = "userOwnerLocator")
    public OwnerLocator<AbstractUser<?>> userOwnerLocator(){
        return new UserOwnerLocator();
    }

    /**
     * Configures an Auditor Aware if missing
     */
    @Bean
    @ConditionalOnMissingBean(name = "rapidAuthSecurityAuditorAware")
    public AuditorAware<Long> rapidSecurityAuditorAware() {
        return new LongIdAuthAuditorAwareImpl();
    }


    @ConditionalOnMissingBean(PermissionEvaluator.class)
    @Bean
    public PermissionEvaluator permissionEvaluator(AclService aclService, @Autowired(required = false) List<GlobalSecurityRule> globalSecurityRules, RapidSecurityContext securityContext){
        return new GlobalRuleEnforcingAclPermissionEvaluator(aclService,globalSecurityRules,securityContext);
    }

    // there can only be ONE Factory
    // if user wishes to create AuthPrincipal differently or with diff subtypes he can define own bean
    @Bean
    @ConditionalOnMissingBean(AuthenticatedPrincipalFactory.class)
    public AuthenticatedPrincipalFactory authenticatedPrincipalFactory(){
        return new AuthenticatedPrincipalFactoryImpl();
    }

    @ConditionalOnMissingBean(name = "denyBlockedGlobalSecurityRule")
    @ConditionalOnProperty(name = "rapid-acl.deny-blocked-rule", havingValue = "true", matchIfMissing = true)
    @Bean
    public DenyBlockedGlobalSecurityRule denyBlockedGlobalSecurityRule(){
        return new DenyBlockedGlobalSecurityRule();
    }

    @ConditionalOnMissingBean(name = "adminGlobalSecurityRule")
    @ConditionalOnProperty(name = "rapid-acl.admin-full-access-rule", havingValue = "true", matchIfMissing = true)
    @Bean
    public AdminGlobalSecurityRule adminGlobalSecurityRule(){
        return new AdminGlobalSecurityRule();
    }

}
