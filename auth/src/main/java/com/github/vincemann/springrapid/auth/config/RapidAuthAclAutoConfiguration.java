package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.acl.service.PermissionStringConverter;
import com.github.vincemann.springrapid.auth.security.GlobalRuleEnforcingAclPermissionEvaluator;
import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.auth.security.GlobalSecurityRule;
import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.AclService;

import java.util.List;

// permission evaluator is overridden this way
@AutoConfigureBefore({RapidAclAutoConfiguration.class})
@ServiceConfig
public class RapidAuthAclAutoConfiguration {

//	@Primary
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    @Bean
    public PermissionEvaluator permissionEvaluator(AclService aclService, @Autowired(required = false) List<GlobalSecurityRule> globalSecurityRules, CrudServiceLocator crudServiceLocator, IdConverter idConverter, PermissionStringConverter permissionStringConverter){
        return new GlobalRuleEnforcingAclPermissionEvaluator(aclService,globalSecurityRules,crudServiceLocator, idConverter, permissionStringConverter);
    }
}
