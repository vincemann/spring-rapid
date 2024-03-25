package com.github.vincemann.springrapid.auth.sec;


import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;

/**
 * Allows user with Admin role to perform any operation.
 */
public class AdminGlobalSecurityRule implements GlobalSecurityRule{

    @Override
    public Boolean checkAccess(IdAwareEntity<?> entity, Object permission) {
        boolean isAdmin = RapidSecurityContext.getRoles().contains(AuthRoles.ADMIN);
        if (isAdmin)
            return true;
        else
            return null;
    }

}
