package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springrapid.acl.framework.VerboseAclPermissionEvaluator;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

/**
 * Denys any access if any user is blocked or if admin is unverified
 * -> unverified Users are let through, so you can treat Role_GoodUser and Role_User differently in access logic.
 *
 * Improves logging
 */
@Slf4j
public class LemonAclPermissionEvaluator extends VerboseAclPermissionEvaluator {

    private RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext;

    public LemonAclPermissionEvaluator(AclService aclService) {
        super(aclService);
    }

    /**
     * Called by Spring Security to evaluate the permission
     *
     * @param auth	Spring Security authentication object,
     * 				from which the current-user can be found
     * @param targetDomainObject	Object for which permission is being checked
     * @param permission			What permission is being checked for, e.g. 'WRITE'
     * @see org.springframework.security.acls.domain.BasePermission
     */
    @Override
    public boolean hasPermission(Authentication auth,
                                 Object targetDomainObject, Object permission) {


        if (targetDomainObject == null)	// if no domain object is provided,
            return true;				// let's pass, allowing the service method
        // to throw a more sensible error message
        performLemonChecks();
        return super.hasPermission(auth,targetDomainObject,permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        performLemonChecks();
        return super.hasPermission(authentication,targetId,targetType,permission);
    }



    //todo is this really the right place for those kind of checks?
    protected void performLemonChecks(){
        //check if blocked or unverified
        LemonAuthenticatedPrincipal principal = securityContext.currentPrincipal();
        if(principal ==null){
            return;
        }
        log.debug("Checking if current User: " + principal.getEmail() + " is blocked or an unverified admin.");
        if(principal.isBlocked()){
            throw new AccessDeniedException("User is Blocked");
        }
        if(principal.isAdmin() && principal.isUnverified()){
            throw new AccessDeniedException("Admin is Unverified");
        }
//        log.debug("Current User is NOT blocked or an unverified admin.");
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext) {
        this.securityContext = securityContext;
    }
}
