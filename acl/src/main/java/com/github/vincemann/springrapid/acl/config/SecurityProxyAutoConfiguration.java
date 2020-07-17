package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.proxy.CrudServiceProxyBeanComposer;
import com.github.vincemann.springrapid.acl.proxy.DefaultSecurityServiceExtensionImpl;
import com.github.vincemann.springrapid.acl.proxy.SecurityServiceProxyFactory;
import com.github.vincemann.springrapid.acl.proxy.DefaultSecurityServiceExtension;
import com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import com.github.vincemann.springrapid.acl.AclSecurityCheckerImpl;
import com.github.vincemann.springrapid.acl.AclSecurityChecker;
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
    public ServiceSecurityRule defaultServiceSecurityRule(){
        return new DefaultSecurityServiceExtensionImpl();
    }

    @ConditionalOnMissingBean(SecurityServiceProxyFactory.class)
    @Bean
    public SecurityServiceProxyFactory crudServiceSecurityProxyFactory(){
        return new SecurityServiceProxyFactory(securityChecker(),defaultServiceSecurityRule());
    }

    @Bean
    @ConditionalOnMissingBean(AclSecurityChecker.class)
    public AclSecurityChecker securityChecker(){
        return new AclSecurityCheckerImpl();
    }

    @Bean
    @ConditionalOnMissingBean(CrudServiceProxyBeanComposer.class)
    public CrudServiceProxyBeanComposer crudServiceProxyBeanComposer(){
        return new CrudServiceProxyBeanComposer(crudServiceSecurityProxyFactory());
    }
}
