package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.repo.UserRepository;
import com.github.vincemann.springrapid.core.Root;
import org.springframework.stereotype.Service;


@Service
@Root
public class MyJpaUserService extends AbstractUserService<User, Long, UserRepository> implements MyUserService{



	@Override
	public User createAdmin(AuthProperties.Admin admin) {
		User createdAdmin = super.createAdmin(admin);
		createdAdmin.setName(admin.getContactInformation().split("@")[0]);
		return createdAdmin;
	}
}