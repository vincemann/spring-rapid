package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.RapidAuthAuthenticatedPrincipal;

public class RapidAuthAuthenticatedPrincipalFactory<U extends AbstractUser<?>>
        implements AuthenticatedPrincipalFactory<RapidAuthAuthenticatedPrincipal, U> {

//    private UserService<U,?> userService;


    @Override
    public RapidAuthAuthenticatedPrincipal create(U user) {
       return new RapidAuthAuthenticatedPrincipal(user.getContactInformation(),user.getPassword(),user
                .getRoles(),user.getId().toString());
    }

//    @Override
//    public U toUser(LemonAuthenticatedPrincipal principal) throws EntityNotFoundException {
//        return userService.findByContactInformation(principal.getContactInformation());
//    }
//
//    @Lazy
//
//    @Autowired
//    public void injectUserService(UserService<U,?> userService) {
//        this.userService = userService;
//    }
}
