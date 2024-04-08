package com.github.vincemann.springrapid.acl.service.sec;

import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import com.github.vincemann.springrapid.auth.Roles;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.service.UserAuthTokenService;
import com.github.vincemann.springrapid.auth.util.Message;
import com.github.vincemann.springrapid.auth.util.VerifyAccess;

public class SecuredUserAuthTokenService implements UserAuthTokenService {

    private UserAuthTokenService decorated;

    public SecuredUserAuthTokenService(UserAuthTokenService decorated) {
        this.decorated = decorated;
    }

    @Override
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException, BadEntityException {
        if (!RapidSecurityContext.getRoles().contains(Roles.ADMIN)){
            // not admin, then can only create token for own user
            VerifyAccess.notNull(RapidSecurityContext.getName(),"must be authenticated");
            VerifyAccess.isTrue(RapidSecurityContext.getName().equals(contactInformation),
                    Message.get("com.github.vincemann.notGoodAdminOrSameUser"));
        }
        return decorated.createNewAuthToken(contactInformation);
    }

    @Override
    public String createNewAuthToken() throws EntityNotFoundException {
        VerifyAccess.isTrue(RapidSecurityContext.isAuthenticated(),"need to be authenticated to create auth token");
        return decorated.createNewAuthToken();
    }
}
