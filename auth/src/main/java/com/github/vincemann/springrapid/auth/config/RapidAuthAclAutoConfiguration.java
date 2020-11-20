package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.security.DenyBlockedAclPermissionEvaluator;
import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.AclService;

//permission evaluator is overridden this way
@AutoConfigureBefore({RapidAclAutoConfiguration.class})
@ServiceConfig
public class RapidAuthAclAutoConfiguration {

//	@Primary
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    @Bean
    public PermissionEvaluator permissionEvaluator(AclService aclService){
        return new DenyBlockedAclPermissionEvaluator(aclService);
    }
}
