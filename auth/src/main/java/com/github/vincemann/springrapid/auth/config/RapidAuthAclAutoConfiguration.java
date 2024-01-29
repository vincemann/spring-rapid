package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.acl.RapidAclSecurityContext;
import com.github.vincemann.springrapid.auth.security.GlobalRuleEnforcingAclPermissionEvaluator;
import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.auth.security.GlobalSecurityRule;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.AclService;

import java.util.List;

// permission evaluator is overridden this way
@AutoConfigureBefore({RapidAclAutoConfiguration.class})
@Configuration
public class RapidAuthAclAutoConfiguration {

//	@Primary
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    @Bean
    public PermissionEvaluator permissionEvaluator(AclService aclService, @Autowired(required = false) List<GlobalSecurityRule> globalSecurityRules, RapidAclSecurityContext<?> securityContext){
        return new GlobalRuleEnforcingAclPermissionEvaluator(aclService,globalSecurityRules,securityContext);
    }
}
