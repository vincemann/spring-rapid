package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.acl.service.AclUserService;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.authdemo.User;
import com.github.vincemann.springrapid.authdemo.UserRepository;
import com.github.vincemann.springrapid.auth.Root;
import org.springframework.stereotype.Service;


@Service
@Root
public class MyUserServiceImpl
		extends AclUserService<User, Long, UserRepository>
		implements MyUserService{



	@Override
	public User createAdmin(AuthProperties.Admin admin) {
		User createdAdmin = super.createAdmin(admin);
		createdAdmin.setName(admin.getContactInformation().split("@")[0]);
		return createdAdmin;
	}
}