package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.security.LemonAclPlugin;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springlemon.auth.security.LemonServiceSecurityRule;
import com.github.vincemann.springrapid.acl.plugin.CleanUpAclPlugin;
import com.github.vincemann.springrapid.acl.proxy.CrudServiceSecurityProxyFactory;
import com.github.vincemann.springrapid.acl.service.AclManaging;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.acl.service.Secured;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyFactory;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.acls.model.MutableAclService;

@ServiceConfig
@Slf4j
public class LemonServiceAutoConfiguration {

    @Autowired
    CrudServiceSecurityProxyFactory securityProxyFactory;

    @Autowired
    LocalPermissionService permissionService;

    @Autowired
    MutableAclService mutableAclService;

    @Autowired
    AbstractUserRepository<?,?> userRepository;

    @Autowired
    MockAuthService mockAuthService;

    public LemonServiceAutoConfiguration() {
        log.info("Created");
    }

    @ConditionalOnMissingBean(LemonServiceSecurityRule.class)
    @Bean
    public LemonServiceSecurityRule lemonServiceSecurityRule(AbstractUserRepository<?,?> repository){
        return new LemonServiceSecurityRule(repository);
    }

    @Bean
    @ConditionalOnMissingBean(LemonAclPlugin.class)
    public LemonAclPlugin lemonAclPlugin(){
        return new LemonAclPlugin(permissionService,mutableAclService,mockAuthService,userRepository);
    }

    @ConditionalOnMissingBean(name = "aclManagingLemonService")
    @Bean
    @AclManaging
    public LemonService<?,?,?> aclManagingLemonService(LemonService<?,?,?> service,
//                                                                            AdminFullAccessAclPlugin adminFullAccess,
//                                                                            AuthenticatedFullAccessAclPlugin authenticatedFullAccessAclPlugin,
                                                       CleanUpAclPlugin cleanUpAclPlugin){
        return ServiceExtensionProxyFactory.create(service/*,adminFullAccess*/,lemonAclPlugin(),/*authenticatedFullAccessAclPlugin,*/cleanUpAclPlugin);
    }


    @ConditionalOnMissingBean(name = "securedLemonService")
    @Bean
    @Secured
    public LemonService<?,?,?> securedLemonService(@AclManaging LemonService<?,?,?> service,
                                                LemonServiceSecurityRule securityRule){
        return securityProxyFactory.create(service,securityRule);
    }
}
