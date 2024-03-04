package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.acldemo.repo.UserRepository;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.JpaUserService;
import com.github.vincemann.springrapid.auth.service.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Root
@Primary
public class MyUserServiceImpl
        extends JpaUserService<User,Long, UserRepository>
            implements MyUserService
{

    @Transactional
    @Override
    public Optional<User> findByLastName(String name) {
        return getRepository().findByLastName(name);
    }
}
