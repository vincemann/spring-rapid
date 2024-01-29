package com.github.vincemann.springrapid.acl.config;


import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@AutoConfigureAfter(RapidAclAutoConfiguration.class)
@Slf4j
public class RapidAclMethodSecurityAutoConfiguration extends GlobalMethodSecurityConfiguration {

    public RapidAclMethodSecurityAutoConfiguration() {

    }


    @Autowired
    MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler;

    @ConditionalOnMissingBean(MethodSecurityExpressionHandler.class)
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return defaultMethodSecurityExpressionHandler;
    }

}

