package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class LemonAuthenticatedPrincipalFactory<U extends AbstractUser<?>>
        implements AuthenticatedPrincipalFactory<LemonAuthenticatedPrincipal, U> {

//    private UserService<U,?> unsecuredUserService;

    @Override
    public LemonAuthenticatedPrincipal create(U user) {
        return new LemonAuthenticatedPrincipal(user);
    }

//    @Override
//    public U toUser(LemonAuthenticatedPrincipal principal) throws EntityNotFoundException {
//        return unsecuredUserService.findByEmail(principal.getEmail());
//    }
//
//    @Lazy
//    @Unsecured
//    @Autowired
//    public void injectUnsecuredUserService(UserService<U,?> userService) {
//        this.unsecuredUserService = userService;
//    }
}
