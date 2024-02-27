package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.sec.Roles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyAccess;

public class SecuredUserAuthTokenService implements UserAuthTokenService{

    private UserAuthTokenService decorated;

    public SecuredUserAuthTokenService(UserAuthTokenService decorated) {
        this.decorated = decorated;
    }

    @Override
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException, BadEntityException {
        if (!RapidSecurityContext.getRoles().contains(Roles.ADMIN)){
            // not admin, then can only create token for own user
            VerifyAccess.notNull(RapidSecurityContext.getName(),"must be authenticated");
            VerifyAccess.condition(RapidSecurityContext.getName().equals(contactInformation),
                    Message.get("com.github.vincemann.notGoodAdminOrSameUser"));
        }
        return decorated.createNewAuthToken(contactInformation);
    }

    @Override
    public String createNewAuthToken() throws EntityNotFoundException {
        VerifyAccess.condition(RapidSecurityContext.isAuthenticated(),"need to be authenticated to create auth token");
        return decorated.createNewAuthToken();
    }
}
