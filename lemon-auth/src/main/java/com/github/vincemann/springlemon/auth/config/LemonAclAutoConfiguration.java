package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.security.DenyBlockedAclPermissionEvaluator;
import com.github.vincemann.springrapid.acl.config.AclAutoConfiguration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.AclService;

//permission evaluator is overridden this way
@AutoConfigureBefore({AclAutoConfiguration.class})
@Configuration
public class LemonAclAutoConfiguration {

//	@Primary
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    @Bean
    public PermissionEvaluator lemonPermissionEvaluator(AclService aclService){
        return new DenyBlockedAclPermissionEvaluator(aclService);
    }
}
