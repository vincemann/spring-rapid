package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.authtest.controller.UserControllerTestTemplate;

public class UserControllerTest extends AbstractControllerIntegrationTest<UserController,MyUserService>
        implements UserControllerTestTemplate<UserController,Long> {


}
