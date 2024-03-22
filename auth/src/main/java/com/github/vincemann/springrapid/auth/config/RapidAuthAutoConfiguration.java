package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.sec.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.AclService;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@AutoConfigureBefore(RapidAclAutoConfiguration.class) // permission evaluator is overridden this way
public class RapidAuthAutoConfiguration {

    @ConditionalOnMissingBean(PermissionEvaluator.class)
    @Bean
    public PermissionEvaluator permissionEvaluator(AclService aclService, @Autowired(required = false) List<GlobalSecurityRule> globalSecurityRules){
        return new GlobalRuleEnforcingAclPermissionEvaluator(aclService,globalSecurityRules);
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


    @ConfigurationProperties("rapid-auth")
    @Bean
    @ConditionalOnMissingBean(name = "authProperties")
    public AuthProperties authProperties(){
        return new AuthProperties();
    }

}
