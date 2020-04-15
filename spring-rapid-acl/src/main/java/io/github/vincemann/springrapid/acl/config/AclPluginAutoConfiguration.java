package io.github.vincemann.springrapid.acl.config;

import io.github.vincemann.springrapid.acl.plugin.AdminFullAccessAclPlugin;
import io.github.vincemann.springrapid.acl.plugin.AdminFullAccessAclPluginImpl;
import io.github.vincemann.springrapid.acl.plugin.AuthenticatedFullAccessAclPlugin;
import io.github.vincemann.springrapid.acl.plugin.AuthenticatedFullAccessAclPluginImpl;
import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.acls.model.MutableAclService;

@ServiceConfig
@AutoConfigureAfter(AclAutoConfiguration.class)
public class AclPluginAutoConfiguration {

    @ConditionalOnMissingBean(LocalPermissionService.class)
    @Bean
    public LocalPermissionService localPermissionService(MutableAclService aclService){
        return new LocalPermissionService(aclService);
    }

    @ConditionalOnMissingBean(AdminFullAccessAclPluginImpl.class)
    @Bean
    public AdminFullAccessAclPluginImpl adminFullAccessAclPlugin(LocalPermissionService localPermissionService, MutableAclService mutableAclService){
        return new AdminFullAccessAclPluginImpl(localPermissionService,mutableAclService);
    }

    @ConditionalOnMissingBean(AuthenticatedFullAccessAclPluginImpl.class)
    @Bean
    public AuthenticatedFullAccessAclPluginImpl authenticatedFullAccessAclPlugin(LocalPermissionService localPermissionService, MutableAclService mutableAclService){
        return new AuthenticatedFullAccessAclPluginImpl(localPermissionService,mutableAclService);
    }
}
