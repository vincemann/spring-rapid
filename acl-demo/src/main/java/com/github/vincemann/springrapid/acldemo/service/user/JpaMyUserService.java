package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.repo.UserRepository;
import com.github.vincemann.springrapid.auth.service.JpaUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
//@Primary
public class JpaMyUserService
		extends JpaUserService<User, Long, UserRepository>
				implements MyUserService {


	@Override
	public Optional<User> findByUuid(String uuid) {
		return getRepository().findByUuid(uuid);
	}


	@Override
	public Class<?> getTargetClass() {
		return JpaMyUserService.class;
	}

}