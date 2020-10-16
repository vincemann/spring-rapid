package com.github.vincemann.springlemon.demo.config;

import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.demo.services.MyUserService;
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
