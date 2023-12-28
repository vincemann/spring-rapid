package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.repository.UserRepository;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@ServiceComponent
@Service
// dont mark with primary, is done internally
//@Primary
@Transactional
public class JpaUserService extends AbstractUserService<User, Long, UserRepository> {

	@Override
    public User newUser() {
        return new User();
    }


	@Override
	public Class<?> getTargetClass() {
		return JpaUserService.class;
	}


	@Override
	public User newAdmin(AuthProperties.Admin admin) {
		User createdAdmin = super.newAdmin(admin);
		createdAdmin.setName(admin.getContactInformation()+"name");
		return createdAdmin;
	}
}