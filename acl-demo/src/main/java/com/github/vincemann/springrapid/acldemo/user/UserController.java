package com.github.vincemann.springrapid.acldemo.user;

import com.github.vincemann.springrapid.acl.SecuredUserController;
import com.github.vincemann.springrapid.acldemo.user.MyUserService;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends SecuredUserController<MyUserService> {
}
