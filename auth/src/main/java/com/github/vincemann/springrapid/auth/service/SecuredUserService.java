package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.model.AbstractUser;

import java.io.Serializable;

public class SecuredUserService
        // looks ugly but is just UserService in raw form
        extends SecuredUserServiceDecorator
        <
                        UserService<AbstractUser<Serializable>, Serializable>,
                        AbstractUser<Serializable>,
                        Serializable
                        >
        implements UserService<AbstractUser<Serializable>, Serializable> {


    public SecuredUserService(UserService<AbstractUser<Serializable>, Serializable> decorated) {
        super(decorated);
    }
}
