package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclSecurityChecker;
import com.github.vincemann.springrapid.acl.AclSecurityCheckerImpl;
import com.github.vincemann.springrapid.acl.proxy.*;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
@Slf4j
public class SecurityProxyAutoConfiguration {

    public SecurityProxyAutoConfiguration() {
        log.info("Created");
    }


    @ConditionalOnMissingBean(name = "defaultServiceSecurityRule")
    @DefaultSecurityServiceExtension
    @Bean
    public SecurityServiceExtension defaultServiceSecurityRule(){
        return new DefaultSecurityServiceExtensionImpl();
    }

    @ConditionalOnMissingBean(SecurityExtensionServiceProxyFactory.class)
    @Bean
    public SecurityExtensionServiceProxyFactory crudServiceSecurityProxyFactory(){
        return new SecurityExtensionServiceProxyFactory(defaultServiceSecurityRule());
    }

    @Bean
    @ConditionalOnMissingBean(AclSecurityChecker.class)
    public AclSecurityChecker aclSecurityChecker(){
        return new AclSecurityCheckerImpl();
    }

    @Bean
    @ConditionalOnMissingBean(CrudServiceProxyBeanComposer.class)
    public CrudServiceProxyBeanComposer crudServiceProxyBeanComposer(){
        return new CrudServiceProxyBeanComposer(crudServiceSecurityProxyFactory());
    }
}
