package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.repo.UserRepository;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.JpaUserService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;



@Service
//@Primary
@Transactional
public class MyJpaUserService extends JpaUserService<User, Long, UserRepository> implements MyUserService {

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
	public Class<?> getTargetClass() {
		return MyJpaUserService.class;
	}

}