package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.controller.owner.AuthOwnerLocator;
import com.github.vincemann.springrapid.auth.controller.owner.UserOwnerLocator;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.AuthHeaderHttpTokenService;
import com.github.vincemann.springrapid.auth.service.token.HttpTokenService;
import com.github.vincemann.springrapid.core.config.RapidCrudControllerAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

//we want to override the OwnerLocator
@AutoConfigureBefore({RapidCrudControllerAutoConfiguration.class})
public class RapidAuthControllerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpTokenService.class)
    public HttpTokenService httpTokenService(){
        return new AuthHeaderHttpTokenService();
    }

    // for finding owner of entities
    @Bean
    @ConditionalOnMissingBean(name = "ownerLocator")
    public OwnerLocator<AuditingEntity> ownerLocator(){
        return new AuthOwnerLocator();
    }

    // for finding owner of users
    @Bean
    @ConditionalOnMissingBean(name = "userOwnerLocator")
    public OwnerLocator<AbstractUser<?>> userOwnerLocator(){
        return new UserOwnerLocator();
    }




}
