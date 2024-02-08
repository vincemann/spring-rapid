package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.service.JpaUserService;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
// dont mark with primary, is done internally
//@Primary
@Transactional
public class MyJpaUserService extends JpaUserService<User, Long, UserRepository> implements MyUserService{

	@Override
    public User newUser() {
        return new User();
    }


	@Override
	public Class<?> getTargetClass() {
		return MyJpaUserService.class;
	}


	@Override
	public User createAdmin(AuthProperties.Admin admin) {
		User createdAdmin = super.createAdmin(admin);
		createdAdmin.setName(admin.getContactInformation()+"name");
		return createdAdmin;
	}
}