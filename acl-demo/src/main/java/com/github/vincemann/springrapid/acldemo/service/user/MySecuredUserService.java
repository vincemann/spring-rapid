package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.AbstractSecuredUserServiceDecorator;
import com.github.vincemann.springrapid.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class MySecuredUserService
        extends AbstractSecuredUserServiceDecorator<MyUserService, User,Long>
        implements MyUserService
{

    public MySecuredUserService(MyUserService decorated) {
        super(decorated);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByLastName(String name) {
        Optional<User> owner = getDecorated().findByLastName(name);
        owner.ifPresent(o -> getAclTemplate().checkPermission(o, BasePermission.READ));
        return owner;
    }
}
