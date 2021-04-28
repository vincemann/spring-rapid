package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.repositories.UserRepository;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

import java.util.Optional;
import java.util.UUID;

//@ServiceComponent
//@Primary
public class MyUserServiceImpl extends AbstractUserService<User, Long, UserRepository> implements MyUserService {

	@Override
    public User newUser() {
        return new User();
    }

	@Override
	public Optional<User> findByUuid(String uuid) {
		return getRepository().findByUuid(uuid);
	}

	@Override
	public User signup(User user) throws BadEntityException, AlreadyRegisteredException {
		user.setUuid(UUID.randomUUID().toString());
		return super.signup(user);
	}

	@Override
	public Long toId(String id) {
		return Long.valueOf(id);
	}

	@Override
	public Class<?> getTargetClass() {
		return MyUserServiceImpl.class;
	}

}