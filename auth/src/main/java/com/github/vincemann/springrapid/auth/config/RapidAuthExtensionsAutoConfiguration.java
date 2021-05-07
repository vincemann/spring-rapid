package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.service.extension.UserHasFullPermissionAboutSelfAclExtension;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@ServiceConfig
public class RapidAuthExtensionsAutoConfiguration {


    @ConditionalOnMissingBean(UserHasFullPermissionAboutSelfAclExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public UserHasFullPermissionAboutSelfAclExtension userHasFullPermissionAboutSelfAclExtension(){
        return new UserHasFullPermissionAboutSelfAclExtension();
    }
}
