package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.service.user.MyUserService;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AbstractUserController<MyUserService> {
}
