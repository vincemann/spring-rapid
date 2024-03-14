package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.sec.AuthenticatedPrincipalFactory;


import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.util.Message;
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

    private UserService userService;
    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;


    @Transactional
    @Override
    public RapidPrincipal loadUserByUsername(String contactInformation) throws UsernameNotFoundException {
        Optional<AbstractUser<?>> user = userService.findByContactInformation(contactInformation);
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
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
