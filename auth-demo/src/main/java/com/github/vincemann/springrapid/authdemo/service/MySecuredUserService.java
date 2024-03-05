package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.auth.service.AbstractSecuredUserServiceDecorator;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.core.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Secured
public class MySecuredUserService
        extends AbstractSecuredUserServiceDecorator<MyUserService, User,Long>
        implements MyUserService
{

    @Autowired
    public MySecuredUserService(@Root MyUserService decorated) {
        super(decorated);
    }
}
