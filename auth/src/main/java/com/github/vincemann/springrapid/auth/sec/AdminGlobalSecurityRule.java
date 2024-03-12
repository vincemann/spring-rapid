package com.github.vincemann.springrapid.auth.sec;


import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;

public class AdminGlobalSecurityRule implements GlobalSecurityRule{

    @Override
    public Boolean checkAccess(IdentifiableEntity<?> entity, Object permission) {
        boolean isAdmin = RapidSecurityContext.getRoles().contains(AuthRoles.ADMIN);
        if (isAdmin)
            return true;
        else
            return null;
    }

}
