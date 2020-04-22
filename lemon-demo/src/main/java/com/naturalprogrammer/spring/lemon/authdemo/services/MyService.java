package com.naturalprogrammer.spring.lemon.authdemo.services;

import com.naturalprogrammer.spring.lemon.authdemo.entities.User;
import com.naturalprogrammer.spring.lemon.auth.service.LemonServiceImpl;
import com.naturalprogrammer.spring.lemon.authdemo.repositories.UserRepository;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MyService extends LemonServiceImpl<User, Long,UserRepository> {

	@Override
    public User newUser() {
        return new User();
    }

	@Override
	public Long toId(String id) {
		return Long.valueOf(id);
	}
}