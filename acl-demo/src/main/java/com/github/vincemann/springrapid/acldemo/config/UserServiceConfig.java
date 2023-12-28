package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.acldemo.service.jpa.MyUserServiceImpl;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@ServiceConfig
public class UserServiceConfig {

//    @Autowired
    private MyUserService userService;


    @Bean
    @Primary
    public UserService myUserService() {
        return createInstance();
//        return abstractUserService;
    }

//    @Bean
//    @Primary
//    public UserService myUserService(AbstractUserService abstractUserService) {
////        return createInstance();
//        return abstractUserService;
//    }


    @Bean
    public MyUserService myUserServiceI(){
        return createInstance();
//        return userService;
    }

//    @Bean
//    public CrudService<User,Long> userCrudService(){
//        return createInstance();
//    }

    protected MyUserService createInstance(){
        if (userService==null){
            userService = new MyUserServiceImpl();
        }
        return userService;
    }

}
