package com.github.vincemann.springrapid.authdemo.config;


import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@ServiceConfig
public class UserServiceConfig {

    private UserService userService;
    @Bean
    @Primary
    public UserService myUserService(){
        return createInstance();
    }

//    @Primary
    @Bean
    public UserService<User,Long> UserService(){
        return createInstance();
    }

    protected UserService createInstance(){
        if (userService==null){
            userService = new MyUserService();
        }
        return userService;
    }


}
