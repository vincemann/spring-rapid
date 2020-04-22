package com.naturalprogrammer.spring.lemon.auth.config;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.security.LemonAclPlugin;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import com.naturalprogrammer.spring.lemon.auth.security.LemonServiceSecurityRule;
import io.github.vincemann.springrapid.acl.plugin.AdminFullAccessAclPlugin;
import io.github.vincemann.springrapid.acl.plugin.CleanUpAclPlugin;
import io.github.vincemann.springrapid.acl.proxy.create.CrudServiceSecurityProxyFactory;
import io.github.vincemann.springrapid.acl.service.AclManaging;
import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.acl.service.Secured;
import io.github.vincemann.springrapid.core.proxy.factory.CrudServicePluginProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.model.MutableAclService;

@Configuration
public class LemonServiceAutoConfiguration {

    @Autowired
    CrudServiceSecurityProxyFactory securityProxyFactory;

    @Autowired
    LocalPermissionService permissionService;

    @Autowired
    MutableAclService mutableAclService;

    @Autowired
    AbstractUserRepository<?,?> userRepository;

    @ConditionalOnMissingBean(LemonServiceSecurityRule.class)
    @Bean
    public LemonServiceSecurityRule lemonServiceSecurityRule(AbstractUserRepository<?,?> repository){
        return new LemonServiceSecurityRule(repository);
    }

    @Bean
    @ConditionalOnMissingBean(LemonAclPlugin.class)
    public LemonAclPlugin lemonAclPlugin(){
        return new LemonAclPlugin(permissionService,mutableAclService,userRepository);
    }

    @ConditionalOnMissingBean(name = "aclManagingLemonService")
    @Bean
    @AclManaging
    public LemonService<?,?,?> aclManagingLemonService(LemonService<?,?,?> service,
                                                AdminFullAccessAclPlugin adminFullAccess,
                                                CleanUpAclPlugin cleanUpAclPlugin){
        return CrudServicePluginProxyFactory.create(service,adminFullAccess,lemonAclPlugin(),cleanUpAclPlugin);
    }


    @ConditionalOnMissingBean(name = "securedLemonService")
    @Bean
    @Secured
    public LemonService<?,?,?> securedLemonService(@AclManaging LemonService<?,?,?> service,
                                                LemonServiceSecurityRule securityRule){
        return securityProxyFactory.create(service,securityRule);
    }
}
