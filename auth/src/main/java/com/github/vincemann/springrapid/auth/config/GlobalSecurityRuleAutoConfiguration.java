package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.security.DenyBlockedGlobalSecurityRule;
import com.github.vincemann.springrapid.auth.security.GlobalSecurityRule;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class GlobalSecurityRuleAutoConfiguration {

    @ConditionalOnMissingBean(name = "denyBlockedGlobalSecurityRule")
    @Bean
    public GlobalSecurityRule denyBlockedGlobalSecurityRule(){
        return new DenyBlockedGlobalSecurityRule();
    }
}
