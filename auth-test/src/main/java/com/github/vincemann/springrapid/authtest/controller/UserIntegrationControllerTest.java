package com.github.vincemann.springrapid.authtest.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.authtest.controller.template.AbstractUserControllerTestTemplate;
import com.github.vincemann.springrapid.authtest.controller.template.UserControllerTestTemplate;

public abstract class UserIntegrationControllerTest<C extends AbstractUserController>
        extends AbstractUserIntegrationControllerTest<C, UserControllerTestTemplate> {

}
