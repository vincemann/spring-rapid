package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.repositories.UserRepository;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@ServiceComponent
@Service
@Primary
@Transactional
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