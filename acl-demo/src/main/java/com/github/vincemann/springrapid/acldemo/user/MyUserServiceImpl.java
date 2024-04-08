package com.github.vincemann.springrapid.acldemo.user;

import com.github.vincemann.springrapid.acl.service.AclUserService;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Root
@Primary
public class MyUserServiceImpl
        extends AclUserService<User,Long, UserRepository>
            implements MyUserService
{

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByLastName(String name) {
        return getRepository().findByLastName(name);
    }
}
