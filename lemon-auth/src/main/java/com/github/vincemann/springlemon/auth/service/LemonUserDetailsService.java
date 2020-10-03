package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.security.PrincipalUserConverter;
import com.github.vincemann.springlemon.exceptions.util.LexUtils;


import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserDetailsService, as required by Spring Security.
 * 
 */
@ServiceComponent
@Slf4j
public class LemonUserDetailsService
		implements UserDetailsService, AopLoggable {

	private UserService unsecuredUserService;
	private PrincipalUserConverter<? extends RapidAuthenticatedPrincipal,AbstractUser<?>> principalUserConverter;

	@Transactional
	@LogInteraction
	@Override
	public RapidAuthenticatedPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
		AbstractUser<?> user;
		try {
			user = findUserByEmail(email);
		} catch (EntityNotFoundException e) {
			throw new UsernameNotFoundException(
					LexUtils.getMessage("com.naturalprogrammer.spring.userNotFound", email)
					,e);
		}

		return principalUserConverter.toPrincipal(user);
	}

	/**
	 * Finds a user by the given username. Override this
	 * if you aren't using email as the username.
	 */
	protected AbstractUser<?> findUserByEmail(String username) throws EntityNotFoundException {
		return unsecuredUserService.findByEmail(username);
	}

	@Autowired
	public void injectPrincipalUserConverter(PrincipalUserConverter<? extends RapidAuthenticatedPrincipal, AbstractUser<?>> principalUserConverter) {
		this.principalUserConverter = principalUserConverter;
	}


	@Autowired
	@Unsecured
	public void injectUnsecuredUserService(UserService unsecuredUserService) {
		this.unsecuredUserService = unsecuredUserService;
	}
}
