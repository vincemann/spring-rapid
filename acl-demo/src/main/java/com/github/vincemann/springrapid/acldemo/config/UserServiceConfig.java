package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.Root;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.acldemo.service.jpa.JpaOwnerService;
import com.github.vincemann.springrapid.acldemo.service.jpa.JpaVetService;
import com.github.vincemann.springrapid.acldemo.service.jpa.sec.SecuredOwnerService;
import com.github.vincemann.springrapid.acldemo.service.jpa.sec.SecuredVetService;
import com.github.vincemann.springrapid.acldemo.service.user.DelegatingUserService;
import com.github.vincemann.springrapid.acldemo.service.user.SecuredDelegatingUserService;
import com.github.vincemann.springrapid.acldemo.service.user.VetSignupService;
import com.github.vincemann.springrapid.auth.boot.AdminInitializer;
import com.github.vincemann.springrapid.auth.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class UserServiceConfig {

    @Bean(name = "securedUserService")
    @Secured
    public UserService securedUserService(){
        return new SecuredDelegatingUserService();
    }

    @Bean
    @Primary
    public UserService userService(){
        return new DelegatingUserService();
    }

    @Bean
    @Root
    public OwnerService ownerService(){
        return new JpaOwnerService();
    }

    @Bean
    @Root
    public VetService vetService(){
        return new JpaVetService();
    }

    @Bean
    @Secured
    public OwnerService securedOwnerService(OwnerService ownerService){
        return new SecuredOwnerService(ownerService);
    }

    @Bean
    @Secured
    public VetService securedVetService(VetService vetService){
        return new SecuredVetService(vetService);
    }



}
