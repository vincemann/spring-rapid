package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authtest.controller.UserMvcControllerTest;
import com.github.vincemann.springrapid.coretest.controller.AbstractMvcCrudControllerTest;
import com.github.vincemann.springrapid.coretest.controller.urlparamid.IntegrationUrlParamIdControllerTest;
import com.github.vincemann.springrapid.coretest.slicing.TestComponent;

public class UserControllerTest extends AbstractControllerIntegrationTest<UserController,MyUserService>
        implements UserMvcControllerTest<UserController,Long> {


}
