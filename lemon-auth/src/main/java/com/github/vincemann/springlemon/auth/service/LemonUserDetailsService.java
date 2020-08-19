package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.exceptions.util.LexUtils;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
@LogInteraction
@Transactional
public class LemonUserDetailsService
	<U extends AbstractUser<ID>, ID extends Serializable>
implements UserDetailsService, AopLoggable {

	private static final Log log = LogFactory.getLog(LemonUserDetailsService.class);

	private final AbstractUserRepository<U,ID> userRepository;
	
	public LemonUserDetailsService(AbstractUserRepository<U, ID> userRepository) {
		
		this.userRepository = userRepository;
		log.info("Created");
	}


	@Override
	public LemonAuthenticatedPrincipal loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
//		log.debug("Loading user having email: " + username);
		
		// delegates to findUserByUsername
		Optional<U> user = findUserByEmail(username);
		if (user.isEmpty()){
			log.debug("Cant find user with username: " + username);
			throw new UsernameNotFoundException(LexUtils.getMessage("com.naturalprogrammer.spring.userNotFound", username));
		}

//		log.debug("Loaded user having username: " + username);

		return new LemonAuthenticatedPrincipal(user.get().toUserDto());
//		log.debug("Loaded principal: " + lemonPrincipal);
//		return lemonPrincipal;
	}

	/**
	 * Finds a user by the given username. Override this
	 * if you aren't using email as the username.
	 */
	public Optional<U> findUserByEmail(String username) {
		return userRepository.findByEmail(username);
	}
}
