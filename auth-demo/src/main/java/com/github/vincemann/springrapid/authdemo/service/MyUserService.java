package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.repositories.UserRepository;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.transaction.annotation.Transactional;

//@ServiceComponent
//@Primary
@Transactional
public class MyUserService extends AbstractUserService<User, Long, UserRepository> {

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
		return MyUserService.class;
	}

	@Override
	public void setBeanName(String name) {
		super.setBeanName(name);
	}

	@Override
	public String getBeanName() {
		return super.getBeanName();
	}

	@Override
	public User newAdmin(AuthProperties.Admin admin) {
		User createdAdmin = super.newAdmin(admin);
		createdAdmin.setName(admin.getContactInformation()+"name");
		return createdAdmin;
	}
}