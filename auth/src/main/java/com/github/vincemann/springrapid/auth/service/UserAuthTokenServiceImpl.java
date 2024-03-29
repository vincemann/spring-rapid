package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.sec.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static com.github.vincemann.springrapid.auth.util.UserUtils.findPresentByContactInformation;

// need this class as a wrapper so I can implement security extension properly
public class UserAuthTokenServiceImpl implements UserAuthTokenService {

    private AbstractUserRepository userRepository;
    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private AuthorizationTokenService authorizationTokenService;

    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.notEmpty(contactInformation,"contact-information");
        AbstractUser user = findPresentByContactInformation(userRepository,contactInformation);
        return authorizationTokenService.createToken(authenticatedPrincipalFactory.create(user));
    }

    public String createNewAuthToken() throws EntityNotFoundException {
        Assert.isTrue(!RapidSecurityContext.getRoles().contains(AuthRoles.ANON),"cannot create token for anon user");
        Assert.isTrue(!RapidSecurityContext.getRoles().contains(AuthRoles.SYSTEM),"cannot create token for system user");
        RapidPrincipal authenticated = RapidSecurityContext.currentPrincipal();
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
    public void setAuthenticatedPrincipalFactory(AuthenticatedPrincipalFactory authenticatedPrincipalFactory) {
        this.authenticatedPrincipalFactory = authenticatedPrincipalFactory;
    }


    @Autowired
    public void setAuthorizationTokenService(AuthorizationTokenService authorizationTokenService) {
        this.authorizationTokenService = authorizationTokenService;
    }

}
