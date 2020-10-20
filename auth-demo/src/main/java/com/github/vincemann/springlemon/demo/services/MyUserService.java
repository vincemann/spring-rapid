package com.github.vincemann.springlemon.demo.services;

import com.github.vincemann.springlemon.demo.domain.MyUser;
import com.github.vincemann.springlemon.demo.repositories.UserRepository;
import com.github.vincemann.springlemon.auth.service.AbstractUserService;

public class MyUserService extends AbstractUserService<MyUser, Long, UserRepository> {

	@Override
    public MyUser newUser() {
        return new MyUser();
    }

	@Override
	public Long toId(String id) {
		return Long.valueOf(id);
	}

	@Override
	public Class<?> getTargetClass() {
		return MyUserService.class;
	}

}