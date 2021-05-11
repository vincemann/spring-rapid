package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclSecurityChecker;
import com.github.vincemann.springrapid.acl.RapidAclSecurityChecker;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
@Slf4j
public class RapidSecurityProxyAutoConfiguration {

    public RapidSecurityProxyAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(AclSecurityChecker.class)
    public AclSecurityChecker aclSecurityChecker(){
        return new RapidAclSecurityChecker();
    }

}
