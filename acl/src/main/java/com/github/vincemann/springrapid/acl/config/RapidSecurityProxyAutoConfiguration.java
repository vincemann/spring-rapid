package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclSecurityChecker;
import com.github.vincemann.springrapid.acl.AclSecurityCheckerImpl;
import com.github.vincemann.springrapid.acl.proxy.*;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.List;

@ServiceConfig
@Slf4j
public class RapidSecurityProxyAutoConfiguration {

    public RapidSecurityProxyAutoConfiguration() {

    }


    @Bean
    @ConditionalOnMissingBean(AclSecurityChecker.class)
    public AclSecurityChecker aclSecurityChecker(){
        return new AclSecurityCheckerImpl();
    }

}
