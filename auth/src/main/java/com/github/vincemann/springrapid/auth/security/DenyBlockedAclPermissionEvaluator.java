package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.acl.framework.VerboseAclPermissionEvaluator;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

/**
 * Denys any access if user is blocked
 */
@Slf4j
public class DenyBlockedAclPermissionEvaluator extends VerboseAclPermissionEvaluator {

//    private RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext;

    public DenyBlockedAclPermissionEvaluator(AclService aclService) {
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
        performGlobalAuthChecks();
        return super.hasPermission(auth,targetDomainObject,permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        performGlobalAuthChecks();
        return super.hasPermission(authentication,targetId,targetType,permission);
    }



    //todo is this really the right place for those kind of checks?
    protected void performGlobalAuthChecks(){
//        //check if blocked or unverified
//        LemonAuthenticatedPrincipal principal = securityContext.currentPrincipal();
//        if(principal ==null){
//            return;
//        }
        //todo pack das auch woanders hin, evtl gibt es für blocked user trotzdem endpunkte wo die report einreichen könnne zb
        String name = RapidSecurityContext.getName();
        boolean blocked = RapidSecurityContext.hasRole(AuthRoles.BLOCKED);
        log.debug("Checking if current User: " + name + " is blocked.");

        if(blocked){
            throw new AccessDeniedException("User is Blocked");
        }
        //todo check das lieber in LemonSecurityCheckerUtil. Es kann doch auch aktionen geben, die ein unverified admin darf evlt..
//        if(principal.isAdmin() && principal.isUnverified()){
//            throw new AccessDeniedException("Admin is Unverified");
//        }
//        log.debug("Current User is NOT blocked or an unverified admin.");
    }

//    @Autowired
//    public void injectSecurityContext(RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext) {
//        this.securityContext = securityContext;
//    }
}
