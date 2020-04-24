package io.github.vincemann.springrapid.acl.config;

import io.github.vincemann.springrapid.acl.proxy.create.CrudServiceProxyBeanComposer;
import io.github.vincemann.springrapid.acl.proxy.create.CrudServiceSecurityProxyFactory;
import io.github.vincemann.springrapid.acl.proxy.rules.DefaultCrudSecurityRule;
import io.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.acl.securityChecker.DefaultSecurityChecker;
import io.github.vincemann.springrapid.acl.securityChecker.SecurityChecker;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
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
