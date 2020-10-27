package com.github.vincemann.springrapid.authdemo.config;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MyUserServiceConfig {

    @Profile("service")
    @Bean
    @Unsecured
    public UserService<?, ?> unsecuredUserService(){
        return new MyUserService();
    }


}
