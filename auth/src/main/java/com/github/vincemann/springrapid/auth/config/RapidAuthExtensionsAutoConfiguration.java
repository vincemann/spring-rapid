package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.service.ext.acl.UserGainsAdminPermissionOnCreated;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@Configuration
public class RapidAuthExtensionsAutoConfiguration {


    @ConditionalOnMissingBean(name = "userGainsAdminPermissionAboutSelfAclExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public UserGainsAdminPermissionOnCreated userGainsAdminPermissionAboutSelfAclExtension(){
        return new UserGainsAdminPermissionOnCreated();
    }
}
