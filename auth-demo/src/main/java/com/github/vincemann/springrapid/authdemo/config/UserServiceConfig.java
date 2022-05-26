package com.github.vincemann.springrapid.authdemo.config;


import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@ServiceConfig
public class UserServiceConfig {

    @Bean
    @Primary
    public UserService myUserService(){
        return new MyUserService();
    }


}
