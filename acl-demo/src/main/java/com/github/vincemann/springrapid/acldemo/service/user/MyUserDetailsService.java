package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.Owner;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.RapidUserDetailsService;
import com.github.vincemann.springrapid.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService extends RapidUserDetailsService {

    // can by any unsecured user service, just pick owner
    @Autowired
    @Owner
    @Root
    @Override
    public void setUserService(UserService userService) {
        super.setUserService(userService);
    }
}
