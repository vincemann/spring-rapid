package com.naturalprogrammer.spring.lemon.authdemo.services;

import com.naturalprogrammer.spring.lemon.authdemo.entities.User;
import com.naturalprogrammer.spring.lemon.auth.service.LemonServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MyService extends LemonServiceImpl<User, Long> {


	//is ok
	@Override
    public User newUser() {
        return new User();
    }

	@Override
	public Long toId(String id) {
		
		return Long.valueOf(id);
	}
}