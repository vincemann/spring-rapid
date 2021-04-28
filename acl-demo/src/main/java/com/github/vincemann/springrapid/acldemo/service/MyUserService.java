package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.auth.service.UserService;

import java.util.Optional;

public interface MyUserService extends UserService<User,Long> {
    public Optional<User> findByUuid(String uuid);
}
