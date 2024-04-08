package com.github.vincemann.springrapid.acldemo.user;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.service.sec.SecuredUserServiceDecorator;
import com.github.vincemann.springrapid.auth.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service(value = "securedUserService") // this specific bean name is needed in order to overwrite default impl bean
@Secured
public class MySecuredUserService
        extends SecuredUserServiceDecorator<MyUserService, User,Long>
        implements MyUserService
{

    @Autowired
    public MySecuredUserService(@Root MyUserService decorated) {
        super(decorated);
    }

    @Transactional(readOnly = true)
    @Override
    @PostAuthorize("returnObject.isPresent() ? hasPermission(returnObject.get(), 'read') : true")
    public Optional<User> findByLastName(String name) {
        return getDecorated().findByLastName(name);
    }
}
