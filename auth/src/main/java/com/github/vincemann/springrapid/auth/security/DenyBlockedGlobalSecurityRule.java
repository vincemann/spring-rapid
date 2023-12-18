package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.acl.AclEvaluationContext;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

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
