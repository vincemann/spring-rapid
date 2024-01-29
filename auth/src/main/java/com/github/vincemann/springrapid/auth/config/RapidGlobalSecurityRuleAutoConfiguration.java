package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.security.AdminGlobalSecurityRule;
import com.github.vincemann.springrapid.auth.security.DenyBlockedGlobalSecurityRule;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ConditionalOnCustomProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class RapidGlobalSecurityRuleAutoConfiguration {

    @ConditionalOnMissingBean(name = "denyBlockedGlobalSecurityRule")
    @Bean
    public DenyBlockedGlobalSecurityRule denyBlockedGlobalSecurityRule(){
        return new DenyBlockedGlobalSecurityRule();
    }

    @ConditionalOnMissingBean(name = "adminGlobalSecurityRule")
    @ConditionalOnCustomProperties(properties = {"rapid-acl.adminFullAccess"})
    @Bean
    public AdminGlobalSecurityRule adminGlobalSecurityRule(){
        return new AdminGlobalSecurityRule();
    }
}
