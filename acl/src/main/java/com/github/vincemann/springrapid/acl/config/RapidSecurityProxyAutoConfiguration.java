package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclSecurityChecker;
import com.github.vincemann.springrapid.acl.AclSecurityCheckerImpl;
import com.github.vincemann.springrapid.acl.proxy.*;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@ServiceConfig
@Slf4j
public class RapidSecurityProxyAutoConfiguration {

    public RapidSecurityProxyAutoConfiguration() {

    }


    @ConditionalOnMissingBean(name = "defaultServiceSecurityRule")
    @DefaultSecurityServiceExtension
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public SecurityServiceExtension<?> defaultServiceSecurityRule(){
        return new AclDefaultSecurityServiceExtension();
    }


    @Bean
    @ConditionalOnMissingBean(AclSecurityChecker.class)
    public AclSecurityChecker aclSecurityChecker(){
        return new AclSecurityCheckerImpl();
    }

    @Bean
    @ConditionalOnMissingBean(CrudServiceProxyBeanComposer.class)
    public CrudServiceProxyBeanComposer crudServiceProxyBeanComposer(){
        return new CrudServiceProxyBeanComposer();
    }
}
