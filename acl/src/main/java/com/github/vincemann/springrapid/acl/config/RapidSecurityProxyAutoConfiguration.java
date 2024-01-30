package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.acl.AclTemplateImpl;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Configuration
@Slf4j
public class RapidSecurityProxyAutoConfiguration {

    public RapidSecurityProxyAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(AclTemplate.class)
    public AclTemplate aclSecurityChecker(){
        return new AclTemplateImpl();
    }

}
