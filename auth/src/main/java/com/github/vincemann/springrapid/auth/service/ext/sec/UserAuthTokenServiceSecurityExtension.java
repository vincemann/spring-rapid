package com.github.vincemann.springrapid.auth.service.ext.sec;

import com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension;
import com.github.vincemann.springrapid.auth.service.UserAuthTokenService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.sec.Roles;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyAccess;

import static com.github.vincemann.springrapid.auth.util.PrincipalUtils.isAdmin;

public class UserAuthTokenServiceSecurityExtension extends SecurityExtension<UserAuthTokenService>
        implements UserAuthTokenService
{


    @Override
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException {
        VerifyAccess.condition(RapidSecurityContext.getName().equals(contactInformation) ||
                RapidSecurityContext.getRoles().contains(Roles.ADMIN),
                Message.get("com.github.vincemann.notGoodAdminOrSameUser"));
        return getNext().createNewAuthToken(contactInformation);
    }

    @Override
    public String createNewAuthToken() throws EntityNotFoundException {
        VerifyAccess.condition(RapidSecurityContext.isAuthenticated(),"need to be authenticated to create auth token");
        return getNext().createNewAuthToken();
    }
}
