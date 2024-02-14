package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.sec.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

// need this class as a wrapper so I can implement security extension properly
public class UserAuthTokenServiceImpl implements UserAuthTokenService {

    private UserService userService;
    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private AuthorizationTokenService authorizationTokenService;

    private RapidSecurityContext securityContext;

    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException {
        Optional<AbstractUser> byContactInformation = userService.findByContactInformation(contactInformation);
        VerifyEntity.isPresent(byContactInformation, "user with contactInformation: " + contactInformation + " not found");
        return authorizationTokenService.createToken(authenticatedPrincipalFactory.create(byContactInformation.get()));
    }

    public String createNewAuthToken() throws EntityNotFoundException {
        if (RapidSecurityContext.getRoles().contains(AuthRoles.ANON))
            throw new IllegalArgumentException("cannot create token for anon user");
        return createNewAuthToken(securityContext.currentPrincipal().getName());
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthenticatedPrincipalFactory(AuthenticatedPrincipalFactory authenticatedPrincipalFactory) {
        this.authenticatedPrincipalFactory = authenticatedPrincipalFactory;
    }


    @Autowired
    public void setAuthorizationTokenService(AuthorizationTokenService authorizationTokenService) {
        this.authorizationTokenService = authorizationTokenService;
    }


    @Autowired
    public void setSecurityContext(RapidSecurityContext securityContext) {
        this.securityContext = securityContext;
    }
}
