package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.acl.AclEvaluationContext;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;

public class AdminGlobalSecurityRule implements GlobalSecurityRule{

    @Override
    public Boolean checkAccess(AclEvaluationContext aclContext) {
        boolean isAdmin = RapidSecurityContext.getRoles().contains(AuthRoles.ADMIN);
        if (isAdmin)
            return true;
        else
            return null;
    }
}
