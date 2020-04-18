package com.naturalprogrammer.spring.lemon.auth.config;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import com.naturalprogrammer.spring.lemon.auth.service.LemonServiceSecurityRule;
import io.github.vincemann.springrapid.acl.plugin.AdminFullAccessAclPlugin;
import io.github.vincemann.springrapid.acl.plugin.AuthenticatedFullAccessAclPlugin;
import io.github.vincemann.springrapid.acl.proxy.create.CrudServiceSecurityProxyFactory;
import io.github.vincemann.springrapid.acl.service.AclManaging;
import io.github.vincemann.springrapid.acl.service.Secured;
import io.github.vincemann.springrapid.core.proxy.factory.CrudServicePluginProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LemonServiceConfig {

    @Autowired
    CrudServiceSecurityProxyFactory securityProxyFactory;

    @ConditionalOnMissingBean(LemonServiceSecurityRule.class)
    @Bean
    public LemonServiceSecurityRule lemonServiceSecurityRule(AbstractUserRepository repository){
        return new LemonServiceSecurityRule(repository);
    }

    @ConditionalOnMissingBean(name = "aclManagingLemonService")
    @Bean
    @AclManaging
    public LemonService aclManagingLemonService(LemonService service,
                                                AdminFullAccessAclPlugin adminFullAccess,
                                                AuthenticatedFullAccessAclPlugin authenticatedFullAccess){
        return CrudServicePluginProxyFactory.create(service,adminFullAccess,authenticatedFullAccess);
    }


    @ConditionalOnMissingBean(name = "securedLemonService")
    @Bean
    @Secured
    public LemonService securedLemonService(@AclManaging LemonService service,
                                                LemonServiceSecurityRule securityRule){
        return securityProxyFactory.create(service,securityRule);
    }
}
