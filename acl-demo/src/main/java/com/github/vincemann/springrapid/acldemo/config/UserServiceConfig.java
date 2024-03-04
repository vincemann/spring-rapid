package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.Owner;
import com.github.vincemann.springrapid.acldemo.Vet;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.user.MySecuredUserService;
import com.github.vincemann.springrapid.acldemo.service.user.MyUserService;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.VerificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class UserServiceConfig {

    @Bean
    @Secured
    public MyUserService securedUserService(@Root MyUserService service) {
        return new MySecuredUserService(service);
    }

}
