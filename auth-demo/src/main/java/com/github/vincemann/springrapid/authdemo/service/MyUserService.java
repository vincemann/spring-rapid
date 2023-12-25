package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.repository.UserRepository;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

@ServiceComponent
@Primary
@Transactional
public class MyUserService extends AbstractUserService<User, Long, UserRepository> implements UserService<User,Long> {

	@Override
    public User newUser() {
        return new User();
    }


	@Override
	public Class<?> getTargetClass() {
		return MyUserService.class;
	}


	@Override
	public User newAdmin(AuthProperties.Admin admin) {
		User createdAdmin = super.newAdmin(admin);
		createdAdmin.setName(admin.getContactInformation()+"name");
		return createdAdmin;
	}
}