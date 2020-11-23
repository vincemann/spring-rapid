package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.proxy.SimpleAclChecksExtension;
import com.github.vincemann.springrapid.acl.service.extensions.AuthenticatedFullAccessAclServiceExtension;
import com.github.vincemann.springrapid.acl.service.extensions.CleanUpAclServiceExtension;
import com.github.vincemann.springrapid.acl.service.extensions.InheritParentAclServiceExtension;
import com.github.vincemann.springrapid.core.config.RapidJsonAutoConfiguration;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@ServiceConfig
@Slf4j
@AutoConfigureAfter(RapidJsonAutoConfiguration.class)
public class RapidAclExtensionsAutoConfiguration {

    public RapidAclExtensionsAutoConfiguration() {

    }

    @ConditionalOnMissingBean(name = "simpleAclChecksExtension")
    @Qualifier("simpleAclChecksExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public AbstractServiceExtension<?,?> simpleAclChecksExtension(){
        return new SimpleAclChecksExtension();
    }

    @ConditionalOnMissingBean(AuthenticatedFullAccessAclServiceExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public AuthenticatedFullAccessAclServiceExtension authenticatedFullAccessAclExtension(){
        return new AuthenticatedFullAccessAclServiceExtension();
    }

    @Bean
    @ConditionalOnMissingBean(InheritParentAclServiceExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public InheritParentAclServiceExtension inheritParentAclExtension(){
        return new InheritParentAclServiceExtension();
    }

    @Bean
    @ConditionalOnMissingBean(CleanUpAclServiceExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CleanUpAclServiceExtension cleanUpAclExtension(){
        return new CleanUpAclServiceExtension();
    }
}
