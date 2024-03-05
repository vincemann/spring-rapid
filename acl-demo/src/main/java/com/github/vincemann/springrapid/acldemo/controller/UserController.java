package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.acldemo.service.user.MyUserService;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.service.SignupService;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController extends AbstractUserController<User,Long,MyUserService> {


    @Override
    public List<String> getIgnoredEndPoints() {
        // signup is implemented in owner & vet controller
        return Lists.newArrayList(getSignupUrl());
    }

    // not needed dont autowire
    @Override
    public void setSignupService(SignupService signupService) {
        super.setSignupService(signupService);
    }
}
