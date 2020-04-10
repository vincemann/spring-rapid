package io.github.vincemann.springrapid.acl.config;


import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


@ServiceConfig
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class AclMethodSecurityAutoConfiguration extends GlobalMethodSecurityConfiguration {

    public AclMethodSecurityAutoConfiguration() {
        log.debug("AclMethodSecurityAutoConfiguration loaded");
    }


    @Autowired
    MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler;

    @ConditionalOnMissingBean(MethodSecurityExpressionHandler.class)
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return defaultMethodSecurityExpressionHandler;
    }

}

