package com.naturalprogrammer.spring.lemon.auth.security;

import com.naturalprogrammer.spring.lemon.auth.security.domain.UserDto;
import com.naturalprogrammer.spring.lemon.auth.util.LecUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

/**
 * Denys any access if blocked or unverified admin
 */
public class LemonPermissionEvaluator extends AclPermissionEvaluator {

    private static final Log log = LogFactory.getLog(LemonPermissionEvaluator.class);

    public LemonPermissionEvaluator(AclService aclService) {
        super(aclService);
        log.debug("created");
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

        log.debug("Checking whether " + auth
                + "\n  has " + permission + " permission for "
                + targetDomainObject);

        if (targetDomainObject == null)	// if no domain object is provided,
            return true;				// let's pass, allowing the service method
        // to throw a more sensible error message

        performChecks(auth);
        return super.hasPermission(auth,targetDomainObject,permission);
    }


    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        performChecks(authentication);
        return super.hasPermission(authentication,targetId,targetType,permission);
    }

    private void performChecks(Authentication authentication){
        //check if blocked or unverified
        UserDto userDto = LecUtils.currentUser(authentication);
        if(userDto==null){
            return;
        }
        if(userDto.isBlocked()){
            throw new AccessDeniedException("User is Blocked");
        }
        if(userDto.isAdmin() && userDto.isUnverified()){
            throw new AccessDeniedException("Admin is Unverified");
        }
    }

}
