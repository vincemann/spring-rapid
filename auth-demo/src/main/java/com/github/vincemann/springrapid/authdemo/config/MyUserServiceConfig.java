package com.github.vincemann.springrapid.authdemo.config;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class MyUserServiceConfig {

    @Profile("service")
    @Bean
    @Primary
    public UserService<?, ?> userService(){
        return new MyUserService();
    }


}
