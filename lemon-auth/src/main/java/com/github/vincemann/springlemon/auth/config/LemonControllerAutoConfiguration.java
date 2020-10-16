package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.controller.UserEndpointInfo;
import com.github.vincemann.springlemon.auth.controller.owner.LemonOwnerLocator;
import com.github.vincemann.springlemon.auth.service.token.AuthHeaderHttpTokenService;
import com.github.vincemann.springlemon.auth.service.token.HttpTokenService;
import com.github.vincemann.springrapid.core.config.RapidControllerAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

//we want to override the OwnerLocator
@AutoConfigureBefore({RapidControllerAutoConfiguration.class})
public class LemonControllerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpTokenService.class)
    public HttpTokenService httpTokenService(){
        return new AuthHeaderHttpTokenService();
    }

    @Bean
    @ConditionalOnMissingBean(name = "lemonOwnerLocator")
    public OwnerLocator lemonOwnerLocator(){
        return new LemonOwnerLocator();
    }


    @Bean
    @ConditionalOnMissingBean(UserEndpointInfo.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public UserEndpointInfo userEndpointInfo(){
        return new UserEndpointInfo();
    }


}
