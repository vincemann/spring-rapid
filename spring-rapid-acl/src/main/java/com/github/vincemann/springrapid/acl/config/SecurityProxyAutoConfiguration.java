package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.proxy.create.CrudServiceProxyBeanComposer;
import com.github.vincemann.springrapid.acl.proxy.create.CrudServiceSecurityProxyFactory;
import com.github.vincemann.springrapid.acl.proxy.rules.DefaultCrudSecurityRule;
import com.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule;
import com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import com.github.vincemann.springrapid.acl.securityChecker.DefaultSecurityChecker;
import com.github.vincemann.springrapid.acl.securityChecker.SecurityChecker;
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
    @DefaultServiceSecurityRule
    @Bean
    public ServiceSecurityRule defaultServiceSecurityRule(){
        return new DefaultCrudSecurityRule();
    }

    @ConditionalOnMissingBean(CrudServiceSecurityProxyFactory.class)
    @Bean
    public CrudServiceSecurityProxyFactory crudServiceSecurityProxyFactory(){
        return new CrudServiceSecurityProxyFactory(securityChecker(),defaultServiceSecurityRule());
    }

    @Bean
    @ConditionalOnMissingBean(SecurityChecker.class)
    public SecurityChecker securityChecker(){
        return new DefaultSecurityChecker();
    }

    @Bean
    @ConditionalOnMissingBean(CrudServiceProxyBeanComposer.class)
    public CrudServiceProxyBeanComposer crudServiceProxyBeanComposer(){
        return new CrudServiceProxyBeanComposer(crudServiceSecurityProxyFactory());
    }
}
