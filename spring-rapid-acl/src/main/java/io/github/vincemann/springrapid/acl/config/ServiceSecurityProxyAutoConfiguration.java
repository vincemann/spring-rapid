package io.github.vincemann.springrapid.acl.config;

import io.github.vincemann.springrapid.acl.proxy.noRuleStrategy.HandleNoSecurityRuleStrategy;
import io.github.vincemann.springrapid.acl.proxy.noRuleStrategy.LoggingHandleNoSecurityRuleStrategy;
import io.github.vincemann.springrapid.acl.proxy.rules.DefaultCrudSecurityRule;
import io.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
@Slf4j
public class ServiceSecurityProxyAutoConfiguration {

    public ServiceSecurityProxyAutoConfiguration() {
        log.debug("ServiceSecurityProxyAutoConfiguration loaded");
    }


    @ConditionalOnMissingBean(HandleNoSecurityRuleStrategy.class)
    @Bean
    public HandleNoSecurityRuleStrategy handleNoSecurityRuleStrategy(){
        return new LoggingHandleNoSecurityRuleStrategy();
    }

    @ConditionalOnMissingBean(name = "defaultServiceSecurityRule")
    @DefaultServiceSecurityRule
    @Bean
    public ServiceSecurityRule defaultServiceSecurityRule(){
        return new DefaultCrudSecurityRule();
    }
}
