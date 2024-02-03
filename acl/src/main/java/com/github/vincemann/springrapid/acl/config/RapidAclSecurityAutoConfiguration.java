package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.acl.AclTemplateImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RapidAclSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(AclTemplate.class)
    public AclTemplate aclTemplate(){
        return new AclTemplateImpl();
    }

}
