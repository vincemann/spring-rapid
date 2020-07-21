package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.security.LemonAclServiceExtension;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springlemon.auth.security.LemonServiceSecurityRule;
import com.github.vincemann.springrapid.acl.plugin.CleanUpAclServiceExtension;
import com.github.vincemann.springrapid.acl.proxy.SecurityExtensionServiceProxyFactory;
import com.github.vincemann.springrapid.acl.proxy.AclManaging;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
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
    SecurityExtensionServiceProxyFactory securityProxyFactory;

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
    @ConditionalOnMissingBean(LemonAclServiceExtension.class)
    public LemonAclServiceExtension lemonAclExtension(){
        return new LemonAclServiceExtension(permissionService,mutableAclService,mockAuthService,userRepository);
    }

    @ConditionalOnMissingBean(name = "aclManagingLemonService")
    @Bean
    @AclManaging
    public LemonService<?,?,?> aclManagingLemonService(LemonService<?,?,?> service,
//                                                                            AdminFullAccessAclExtension adminFullAccess,
//                                                                            AuthenticatedFullAccessAclExtension authenticatedFullAccessAclExtension,
                                                       CleanUpAclServiceExtension cleanUpAclExtension){
        return ServiceExtensionProxyBuilder.create(service/*,adminFullAccess*/,lemonAclExtension(),/*authenticatedFullAccessAclExtension,*/cleanUpAclExtension);
    }


    @ConditionalOnMissingBean(name = "securedLemonService")
    @Bean
    @Secured
    public LemonService<?,?,?> securedLemonService(@AclManaging LemonService<?,?,?> service,
                                                LemonServiceSecurityRule securityRule){
        return securityProxyFactory.create(service,securityRule);
    }
}
