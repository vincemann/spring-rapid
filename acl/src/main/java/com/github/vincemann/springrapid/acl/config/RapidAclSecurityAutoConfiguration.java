package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.RapidAclSecurityChecker;
import com.github.vincemann.springrapid.acl.RapidAclSecurityContext;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RapidAclSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(RapidSecurityContext.class)
    public RapidAclSecurityContext<?> rapidSAclSecurityContext(){
        return new RapidAclSecurityContext<>();
    }

}
