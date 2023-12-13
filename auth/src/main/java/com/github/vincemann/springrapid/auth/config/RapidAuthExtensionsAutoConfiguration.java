package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.service.extension.UserGainsAdminPermissionAboutSelfAclExtension;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@ServiceConfig
public class RapidAuthExtensionsAutoConfiguration {


    @ConditionalOnMissingBean(UserGainsAdminPermissionAboutSelfAclExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public UserGainsAdminPermissionAboutSelfAclExtension userHasFullPermissionAboutSelfAclExtension(){
        return new UserGainsAdminPermissionAboutSelfAclExtension();
    }
}
