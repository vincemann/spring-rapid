package com.github.vincemann.springrapid.authdemo.config;


import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@ServiceConfig
public class MyUserServiceConfig {

    @Bean
    @Primary
    public UserService userService(){
        return new MyUserService();
    }


}
