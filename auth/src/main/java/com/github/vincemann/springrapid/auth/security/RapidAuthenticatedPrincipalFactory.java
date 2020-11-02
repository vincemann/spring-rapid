package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.IdConverter;
import com.github.vincemann.springrapid.auth.domain.RapidAuthAuthenticatedPrincipal;

public class RapidAuthenticatedPrincipalFactory<U extends AbstractUser<?>>
        implements AuthenticatedPrincipalFactory<RapidAuthAuthenticatedPrincipal, U> {

//    private UserService<U,?> unsecuredUserService;


    @Override
    public RapidAuthAuthenticatedPrincipal create(U user) {
        return new RapidAuthAuthenticatedPrincipal(user);
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
