package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authdemo.model.User;

public interface MyUserService extends UserService<User,Long> {
}
