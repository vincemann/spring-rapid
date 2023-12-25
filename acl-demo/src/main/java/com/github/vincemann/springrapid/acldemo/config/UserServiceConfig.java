package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.acldemo.service.jpa.MyUserServiceImpl;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

//@ServiceConfig
//public class UserServiceConfig {
//
//    private UserService userService;
//
//
//    @Bean
//    @Primary
//    public UserService myUserService() {
//        return createInstance();
//    }
//
//    @Bean
//    public CrudService<User,Long> userCrudService(){
//        return createInstance();
//    }
//
//    protected UserService createInstance(){
//        if (userService==null){
//            userService = new MyUserServiceImpl();
//        }
//        return userService;
//    }
//
//}
