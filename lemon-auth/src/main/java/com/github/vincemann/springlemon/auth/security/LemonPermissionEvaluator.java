package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.util.LecUtils;
import com.github.vincemann.springlemon.auth.util.LecwUtils;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.ObjectIdentityRetrievalStrategyImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Denys any access if any user is blocked or if admin is unverified
 * -> unverified Users are let through, so you can treat Role_GoodUser and Role_User differently in access logic.
 *
 * Improves logging
 */
@Slf4j
public class LemonPermissionEvaluator extends AclPermissionEvaluator implements AopLoggable {

    private final AclService aclService;
    private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy = new ObjectIdentityRetrievalStrategyImpl();
    private ObjectIdentityGenerator objectIdentityGenerator = new ObjectIdentityRetrievalStrategyImpl();
    private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
    private PermissionFactory permissionFactory = new DefaultPermissionFactory();
    private CrudServiceLocator crudServiceLocator;

    public LemonPermissionEvaluator(AclService aclService, CrudServiceLocator crudServiceLocator) {
        super(aclService);
        this.aclService=aclService;
        this.crudServiceLocator = crudServiceLocator;
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
    @LogInteraction
    @Override
    public boolean hasPermission(Authentication auth,
                                 Object targetDomainObject, Object permission) {

        if (targetDomainObject == null)	// if no domain object is provided,
            return true;				// let's pass, allowing the service method
        // to throw a more sensible error message

        performChecks(auth);
        ObjectIdentity objectIdentity = objectIdentityRetrievalStrategy
                .getObjectIdentity(targetDomainObject);

        return checkPermission(auth, objectIdentity, permission);
    }

    @LogInteraction
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        performChecks(authentication);
        ObjectIdentity objectIdentity = objectIdentityGenerator.createObjectIdentity(
                targetId, targetType);
        return checkPermission(authentication, objectIdentity, permission);
    }


    private boolean checkPermission(Authentication authentication, ObjectIdentity oid,
                                    Object permission) {
        // Obtain the SIDs applicable to the principal

        List<Sid> sids = sidRetrievalStrategy.getSids(authentication);
        List<Permission> requiredPermission = resolvePermission(permission);

        if (log.isTraceEnabled()){
            String authenticated = RapidSecurityContext.getName();
            log.trace("Does authenticated User: " + authenticated + " have permissions: " + requiredPermission +"\n"+
                    "that are required for an operation over: " + );
        }

        try {
            // Lookup only ACLs for SIDs we're interested in
            Acl acl = aclService.readAclById(oid, sids);

            if (acl.isGranted(requiredPermission, sids, false)) {
                if (debug) {
                    logger.debug("Access is granted");
                }

                return true;
            }

            if (debug) {
                logger.debug("Returning false - ACLs returned, but insufficient permissions for this principal");
            }

        }
        catch (NotFoundException nfe) {
            if (debug) {
                logger.debug("Returning false - no ACLs apply for this principal");
            }
        }

        return false;

    }

    List<Permission> resolvePermission(Object permission) {
        if (permission instanceof Integer) {
            return Arrays.asList(permissionFactory.buildFromMask((Integer) permission));
        }

        if (permission instanceof Permission) {
            return Arrays.asList((Permission) permission);
        }

        if (permission instanceof Permission[]) {
            return Arrays.asList((Permission[]) permission);
        }

        if (permission instanceof String) {
            String permString = (String) permission;
            Permission p;

            try {
                p = permissionFactory.buildFromName(permString);
            }
            catch (IllegalArgumentException notfound) {
                p = permissionFactory.buildFromName(permString.toUpperCase(Locale.ENGLISH));
            }

            if (p != null) {
                return Arrays.asList(p);
            }

        }
        throw new IllegalArgumentException("Unsupported permission: " + permission);
    }




    private void performChecks(Authentication authentication){
        //check if blocked or unverified
        LemonUserDto lemonUserDto = LecUtils.currentUser(authentication);
        log.debug("Checking if current User: " + lemonUserDto.getEmail() + " is blocked or an unverified admin.");
        if(lemonUserDto ==null){
            return;
        }
        if(lemonUserDto.isBlocked()){
            throw new AccessDeniedException("User is Blocked");
        }
        if(lemonUserDto.isAdmin() && lemonUserDto.isUnverified()){
            throw new AccessDeniedException("Admin is Unverified");
        }
        log.debug("Current User is NOT blocked or an unverified admin.");
    }

}
