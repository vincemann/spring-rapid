package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.sec.AdminGlobalSecurityRule;
import com.github.vincemann.springrapid.auth.sec.DenyBlockedGlobalSecurityRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class RapidGlobalSecurityRuleAutoConfiguration {

    @ConditionalOnMissingBean(name = "denyBlockedGlobalSecurityRule")
    @Bean
    public DenyBlockedGlobalSecurityRule denyBlockedGlobalSecurityRule(){
        return new DenyBlockedGlobalSecurityRule();
    }

    @ConditionalOnMissingBean(name = "adminGlobalSecurityRule")
    @ConditionalOnProperty(name = "rapid-acl.admin-full-access", havingValue = "true", matchIfMissing = true)
    @Bean
    public AdminGlobalSecurityRule adminGlobalSecurityRule(){
        return new AdminGlobalSecurityRule();
    }
}
