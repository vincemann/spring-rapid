package com.github.vincemann.springrapid.auth.config;


import com.github.vincemann.springrapid.auth.controller.owner.AuthOwnerLocator;
import com.github.vincemann.springrapid.auth.controller.owner.UserOwnerLocator;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.config.RapidCrudControllerAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

//we want to override the OwnerLocator
@AutoConfigureBefore({RapidCrudControllerAutoConfiguration.class})
public class RapidAuthControllerAutoConfiguration {
    

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
