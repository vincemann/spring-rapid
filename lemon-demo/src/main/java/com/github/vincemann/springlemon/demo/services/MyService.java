package com.github.vincemann.springlemon.demo.services;

import com.github.vincemann.springlemon.demo.domain.User;
import com.github.vincemann.springlemon.demo.repositories.UserRepository;
import com.github.vincemann.springlemon.auth.service.LemonServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MyService extends LemonServiceImpl<User, Long, UserRepository> {

	@Override
    public User newUser() {
        return new User();
    }

	@Override
	public Long toId(String id) {
		return Long.valueOf(id);
	}

	@Override
	public Class<?> getTargetClass() {
		return MyService.class;
	}
}