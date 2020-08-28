package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.exceptions.util.LexUtils;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * UserDetailsService, as required by Spring Security.
 * 
 * @author Sanjay Patel
 * @modified vincemann
 */
@ServiceComponent
@Slf4j
public class LemonUserDetailsService
	<U extends AbstractUser<ID>, ID extends Serializable>
				implements UserDetailsService, AopLoggable {
	private SimpleLemonService<U,ID> unsecuredLemonService;


	@Transactional
	@LogInteraction
	@Override
	public LemonAuthenticatedPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
		U user;
		try {
			user = findUserByEmail(email);
		} catch (EntityNotFoundException e) {
			log.debug("Cant find user with email: " + email);
			throw new UsernameNotFoundException(LexUtils.getMessage("com.naturalprogrammer.spring.userNotFound", email),e);
		}

		return new LemonAuthenticatedPrincipal(user);
	}

	/**
	 * Finds a user by the given username. Override this
	 * if you aren't using email as the username.
	 */
	protected U findUserByEmail(String username) throws EntityNotFoundException {
		return unsecuredLemonService.findByEmail(username);
	}

	@Autowired
	public void injectUnsecuredLemonService(SimpleLemonService<U, ID> unsecuredLemonService) {
		this.unsecuredLemonService = unsecuredLemonService;
	}
}
