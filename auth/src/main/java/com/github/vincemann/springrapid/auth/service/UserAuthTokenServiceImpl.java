package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AuthPrincipal;
import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import com.github.vincemann.springrapid.acl.Roles;
import com.github.vincemann.springrapid.auth.*;
import com.github.vincemann.springrapid.auth.jwt.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static com.github.vincemann.springrapid.auth.util.UserUtils.findPresentByContactInformation;

// need this class as a wrapper so I can implement security extension properly
public class UserAuthTokenServiceImpl implements UserAuthTokenService {

    private AbstractUserRepository userRepository;
    private AuthPrincipalFactory authPrincipalFactory;
    private AuthorizationTokenService authorizationTokenService;

    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.notEmpty(contactInformation,"contact-information");
        AbstractUser user = findPresentByContactInformation(userRepository,contactInformation);
        return authorizationTokenService.createToken(authPrincipalFactory.create(user));
    }

    public String createNewAuthToken() throws EntityNotFoundException {
        Assert.isTrue(!RapidSecurityContext.getRoles().contains(Roles.ANON),"cannot create token for anon user");
        Assert.isTrue(!RapidSecurityContext.getRoles().contains(Roles.SYSTEM),"cannot create token for system user");
        AuthPrincipal authenticated = RapidSecurityContext.currentPrincipal();
        Assert.isTrue(authenticated != null,"must be authenticated");
        try {
            return createNewAuthToken(authenticated.getName());
        } catch (BadEntityException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setUserRepository(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setAuthenticatedPrincipalFactory(AuthPrincipalFactory authPrincipalFactory) {
        this.authPrincipalFactory = authPrincipalFactory;
    }


    @Autowired
    public void setAuthorizationTokenService(AuthorizationTokenService authorizationTokenService) {
        this.authorizationTokenService = authorizationTokenService;
    }

}
