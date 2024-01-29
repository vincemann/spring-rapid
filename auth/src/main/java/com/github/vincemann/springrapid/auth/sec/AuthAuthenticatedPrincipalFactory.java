package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthAuthenticatedPrincipalImpl;

public class AuthAuthenticatedPrincipalFactory<U extends AbstractUser<?>>
        implements AuthenticatedPrincipalFactory<AuthAuthenticatedPrincipalImpl, U> {

//    private UserService<U,?> userService;


    @Override
    public AuthAuthenticatedPrincipalImpl create(U user) {
       return new AuthAuthenticatedPrincipalImpl(user.getContactInformation(),user.getPassword(),user
                .getRoles(),user.getId() == null ? null : user.getId().toString());
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
