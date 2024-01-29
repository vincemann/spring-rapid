package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.acl.AclEvaluationContext;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

@Slf4j
public class DenyBlockedGlobalSecurityRule implements GlobalSecurityRule {

    @Override
    public Boolean checkAccess(AclEvaluationContext aclEvaluationContext) {
        String name = RapidSecurityContext.getName();
        boolean blocked = RapidSecurityContext.hasRole(AuthRoles.BLOCKED);
        log.debug("Checking if current User: " + name + " is blocked.");

        if(blocked){
            throw new AccessDeniedException("User is Blocked");
        }
        return null;
    }
}
