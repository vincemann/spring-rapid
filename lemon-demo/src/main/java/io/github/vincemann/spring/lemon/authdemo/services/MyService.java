package io.github.vincemann.spring.lemon.authdemo.services;

import io.github.vincemann.spring.lemon.authdemo.domain.User;
import io.github.spring.lemon.auth.service.LemonServiceImpl;
import io.github.vincemann.spring.lemon.authdemo.repositories.UserRepository;
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
}