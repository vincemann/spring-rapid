package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.AuthenticatedPrincipalFactory;


import com.github.vincemann.springrapid.auth.AuthPrincipal;
import com.github.vincemann.springrapid.auth.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UserDetailsService, as required by Spring Security.
 */
public class RapidUserDetailsService
        implements UserDetailsService {

    private AbstractUserRepository userRepository;
    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;


    @Transactional
    @Override
    public AuthPrincipal loadUserByUsername(String contactInformation) throws UsernameNotFoundException {
        Optional<AbstractUser<?>> user = userRepository.findByContactInformation(contactInformation);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(
                    Message.get("com.github.vincemann.userNotFound", contactInformation));
        }
        return authenticatedPrincipalFactory.create(user.get());
    }

    @Autowired
    public void setAuthenticatedPrincipalFactory(AuthenticatedPrincipalFactory authenticatedPrincipalFactory) {
        this.authenticatedPrincipalFactory = authenticatedPrincipalFactory;
    }

    @Autowired
    public void setUserRepository(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
