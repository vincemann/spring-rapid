package com.github.vincemann.springrapid.auth.sec;


import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

@Slf4j
public class DenyBlockedGlobalSecurityRule implements GlobalSecurityRule {

    @Override
    public Boolean checkAccess(IdentifiableEntity<?> entity, Object permission, RapidSecurityContext securityContext) {
        String name = RapidSecurityContext.getName();
        boolean blocked = RapidSecurityContext.hasRole(AuthRoles.BLOCKED);
        log.debug("Checking if current User: " + name + " is blocked.");

        if(blocked){
            throw new AccessDeniedException("User is Blocked");
        }
        return null;
    }

}
