package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.auth.service.UserService;

import java.util.Optional;

public interface MyUserService extends UserService<User,Long> {
    public Optional<User> findByLastName(String name);
}
