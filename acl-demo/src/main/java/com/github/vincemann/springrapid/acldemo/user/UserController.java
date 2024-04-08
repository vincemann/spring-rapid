package com.github.vincemann.springrapid.acldemo.user;

import com.github.vincemann.springrapid.acl.AclUserController;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AclUserController<MyUserService> {
}
